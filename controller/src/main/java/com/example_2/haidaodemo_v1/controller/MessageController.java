package com.example_2.haidaodemo_v1.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example_2.haidaodemo_v1.VO.ChatSessionVO;
import com.example_2.haidaodemo_v1.VO.MessageVO;
import com.example_2.haidaodemo_v1.VO.UserVO;
import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.pojo.Message;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.service.MessageService;
import com.example_2.haidaodemo_v1.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.example_2.haidaodemo_v1.constant.RedisConstants.*;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/history")
    public Result<List<MessageVO>> getChatHistory(@RequestParam Long targetId) {
        Long userId = BaseContext.getUserId();
        stringRedisTemplate.opsForHash()
                .delete(CHAT_UNREAD_KEY+userId,targetId.toString());

        List<Message> res = messageService.list(
                new LambdaQueryWrapper<Message>().
                        and(wrapper ->
                               wrapper.eq(Message::getToUserId,targetId)
                                       .eq(Message::getFromUserId,userId)).
                        or(wrapper ->
                                wrapper.eq(Message::getFromUserId,targetId)
                                        .eq(Message::getToUserId,userId)).
                        orderByAsc(Message::getCreateTime)
        );

        List<MessageVO> messageVOList = new ArrayList<>();
        messageVOList = res.stream().map(message -> {
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message,messageVO);
            UserVO userVO = userService.getUserFromCache(message.getFromUserId());
            messageVO.setFromAvatar(userVO.getAvatar());
            messageVO.setFromNickName(userVO.getNickName());
            return messageVO;
        }).toList();
        return Result.success(messageVOList);
    }

    @RequestMapping("/recent")
    public Result<List<ChatSessionVO>> getChatRecent() {
        Long userId = BaseContext.getUserId();
        Set<String> recentIds = stringRedisTemplate.opsForZSet()
                .reverseRange(CHAT_RECENT_KEY+userId,0,-1);
        if(recentIds==null || recentIds.isEmpty()){
            return Result.success(new ArrayList<>());
        }
        List<Object> curRes = stringRedisTemplate.opsForHash()
                .multiGet(CHAT_SESSION_KEY+userId,new ArrayList<>(recentIds));
        List<ChatSessionVO> res = curRes.stream()
                .filter(Objects::nonNull)
                .map(e->
                {
                    ChatSessionVO vo = new ChatSessionVO();
                    try {
                        vo = objectMapper
                                .readValue(e.toString(),ChatSessionVO.class);
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }

                    Object count = stringRedisTemplate.opsForHash()
                                    .get(CHAT_UNREAD_KEY+userId,
                                        vo.getTargetId().toString());
                    vo.setNotRead(count==null?0:Integer.parseInt(count.toString()));
                    return vo;
                })
                .toList();
        return Result.success(res);
    }

}
