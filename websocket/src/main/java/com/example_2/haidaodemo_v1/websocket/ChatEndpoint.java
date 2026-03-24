package com.example_2.haidaodemo_v1.websocket;

import com.example_2.haidaodemo_v1.VO.ChatSessionVO;
import com.example_2.haidaodemo_v1.VO.UserVO;
import com.example_2.haidaodemo_v1.common.DTO.ChatMessageDTO;
import com.example_2.haidaodemo_v1.pojo.Message;
import com.example_2.haidaodemo_v1.service.MessageService;
import com.example_2.haidaodemo_v1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example_2.haidaodemo_v1.constant.RedisConstants.*;

@Component
@Slf4j
@ServerEndpoint("/ws/socket/{userId}")
public class ChatEndpoint {

    public ChatEndpoint() {
        System.out.println("ChatEndpoint启动成功");
    }
    private static final Map<String, Session> onlineUsers =
            new ConcurrentHashMap<>();

    private static MessageService messageService;
    private static UserService userService;
    private static StringRedisTemplate stringRedisTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    public void setMessageService(MessageService messageService) {
        ChatEndpoint.messageService = messageService;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        ChatEndpoint.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setUserService(UserService userService) {
        ChatEndpoint.userService = userService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        onlineUsers.put(userId, session);
        log.info("新用户上线：{}当前在线人数：{}", userId, onlineUsers.size());
    }

    @OnMessage
    public void onMessage(String message, Session session,
                          @PathParam("userId") String userId) {
        try {
            ChatMessageDTO dto = objectMapper.readValue(message, ChatMessageDTO.class) ;
            String toUserId = dto.getToUserId();
            String content = dto.getContent();
            String parentId = dto.getParentId();
            if (userId.equals(toUserId)) {
                log.warn("检测到用户 {} 尝试对自己发消息，已拦截。", userId);
                return;
            }
            stringRedisTemplate.opsForHash()
                    .increment(CHAT_UNREAD_KEY+toUserId,userId,1);

            long now = System.currentTimeMillis();

            stringRedisTemplate.opsForZSet().add(CHAT_RECENT_KEY+userId,
                    toUserId, now);

            stringRedisTemplate.opsForZSet().add(CHAT_RECENT_KEY+toUserId,
                    userId, now);

            stringRedisTemplate.opsForZSet().removeRange(CHAT_RECENT_KEY+userId,0,-101);

            Message msg = new Message();
            msg.setToUserId(Long.valueOf(toUserId));
            msg.setContent(content);
            msg.setFromUserId(Long.valueOf(userId));
            msg.setCreateTime(LocalDateTime.now());
            msg.setIsRead(false);
            if(parentId!=null){
                msg.setParentId(Long.valueOf(parentId));
            }
            messageService.save(msg);

            String sessionKey = CHAT_SESSION_KEY + userId;
            ChatSessionVO vo = new ChatSessionVO();
            vo.setTargetId(Long.valueOf(toUserId));
            vo.setRecentTime(LocalDateTime.now());
            vo.setRecentContent(content);
            UserVO userVO = userService.getUserFromCache(Long.valueOf(toUserId));
            vo.setFromNickName(userVO.getNickName());
            vo.setFromAvatar(userVO.getAvatar());

            stringRedisTemplate.opsForHash().put(sessionKey, toUserId,
                    objectMapper.writeValueAsString(vo));

            ChatSessionVO toVo = new ChatSessionVO();
            toVo.setTargetId(Long.valueOf(userId));
            toVo.setRecentTime(LocalDateTime.now());
            toVo.setRecentContent(content);
            UserVO tarVO = userService.getUserFromCache(Long.valueOf(userId));
            toVo.setFromNickName(tarVO.getNickName());
            toVo.setFromAvatar(tarVO.getAvatar());
            stringRedisTemplate.opsForHash().put(CHAT_SESSION_KEY+toUserId,
                    userId,objectMapper.writeValueAsString(toVo));

            Session targetSession = onlineUsers.get(toUserId);
            if(targetSession!=null&&targetSession.isOpen()){
                targetSession.getBasicRemote()
                        .sendText(objectMapper.writeValueAsString(msg));
                log.info("发送成功:{} -> {}",userId,toUserId);
            }else{
                log.info("用户:{}不在线",toUserId);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("发送消息失败或者解析失败");
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        log.info("用户:{}断开链接",userId);
        onlineUsers.remove(userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("网络出现问题：{}",error.getMessage());
    }
}
