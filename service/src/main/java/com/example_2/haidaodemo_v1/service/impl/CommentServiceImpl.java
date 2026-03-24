package com.example_2.haidaodemo_v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example_2.haidaodemo_v1.VO.CommentVO;
import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.mapper.CommentMapper;
import com.example_2.haidaodemo_v1.pojo.Comment;
import com.example_2.haidaodemo_v1.pojo.User;
import com.example_2.haidaodemo_v1.service.CommentService;
import com.example_2.haidaodemo_v1.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example_2.haidaodemo_v1.constant.RedisConstants.COMMENT_LIKE_CHANGE_KEY;
import static com.example_2.haidaodemo_v1.constant.RedisConstants.COMMENT_LIKE_KEY;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<CommentVO> getCommentsByPostId(Long postId,String sort) {
        List<Comment> comments;

        if("like".equals(sort)){
            comments = this.list(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getPostId,postId).orderByDesc(Comment::getLikes));
        }else{
            comments = this.list(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getPostId,postId).orderByDesc(Comment::getCreateTime));
        }


        if(comments == null || comments.isEmpty()) return new ArrayList<>();

        Set<Long> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        List<String> userIdStrs = userIds.stream().map(String::valueOf).collect(Collectors.toList());

        Map<Long, User> userMap = userService.listByIds(userIdStrs).stream()
                .collect(Collectors.toMap(User::getId,u->u));

        Long curUserId = BaseContext.getUserId();
        return comments.stream().map(comment -> {
            CommentVO res = new CommentVO();
            BeanUtils.copyProperties(comment, res);
            res.setLikes(this.countLikes(res.getId()));

            if(curUserId != null){
                String likeKey = COMMENT_LIKE_KEY + res.getId();
                Boolean isMember = stringRedisTemplate.opsForSet()
                        .isMember(likeKey, String.valueOf(curUserId));
                res.setIsLiked(Boolean.TRUE.equals(isMember));
            }else{
                res.setIsLiked(Boolean.FALSE);
            }
            User user = userMap.get(comment.getUserId());
            if (user != null) {
                res.setId(comment.getId());
                res.setUserAvatar(user.getAvatar());
                res.setUserNickname(user.getNickName());
            }
            return res;
        }).collect(Collectors.toList());
    }

    @Override
    public void likeComment(Long commentId) {
        Long curUserId = BaseContext.getUserId();
        String key = COMMENT_LIKE_KEY + commentId;

        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(curUserId));
        if(Boolean.TRUE.equals(isMember)){
            stringRedisTemplate.opsForSet().remove(key, String.valueOf(curUserId));
        }else{
            stringRedisTemplate.opsForSet().add(key, String.valueOf(curUserId));
        }

        stringRedisTemplate.opsForSet()
                .add(COMMENT_LIKE_CHANGE_KEY, String.valueOf(commentId));
    }

    @Override
    public Long countLikes(Long commentId) {
        String key = COMMENT_LIKE_KEY + commentId;
        return stringRedisTemplate.opsForSet().size(key);
    }


}
