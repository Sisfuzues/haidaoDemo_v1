package com.example_2.haidaodemo_v1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example_2.haidaodemo_v1.VO.PostVO;
import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.mapper.PostMapper;
import com.example_2.haidaodemo_v1.pojo.Post;
import com.example_2.haidaodemo_v1.pojo.User;
import com.example_2.haidaodemo_v1.service.PostService;
import com.example_2.haidaodemo_v1.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example_2.haidaodemo_v1.constant.RedisConstants.POST_LIKE_KEY;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper,Post> implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PostVO findById(Long id) {
        Post post = postMapper.selectById(id);
        if(post == null){
            return null;
        }

        PostVO res = new PostVO();
        BeanUtils.copyProperties(post,res);
        Long count = stringRedisTemplate.opsForSet().size(POST_LIKE_KEY+post.getId());
        res.setLikeCount(count!=null?count:0);

        Long curUserId = BaseContext.getUserId();
        if(curUserId!=null){
            Boolean liked = stringRedisTemplate.opsForSet()
                    .isMember(POST_LIKE_KEY+post.getId(),String.valueOf(curUserId) );
            res.setHasLiked(Boolean.TRUE.equals(liked));
        }else{
            res.setHasLiked(false);
        }

        User user = userService.getUserById(String.valueOf(post.getUserId()));
        if(user!=null){
            res.setUserId(user.getId());
            res.setUserName(user.getNickName());
            res.setUserAvatar(user.getAvatar());
        }
        return res;
    }

    @Override
    public List<PostVO> findAll() {
        List<Post> posts = postMapper.selectList(null);
        if(posts==null || posts.isEmpty()){
            return new ArrayList<>();
        }

        Set<String> userIds = posts.stream()
                                   .map(Post::getUserId)
                                   .map(String::valueOf)
                                   .collect(Collectors.toSet());
        List<User> users = userService.listByIds(new ArrayList<>(userIds));

        Map<Long, User> userMap = new HashMap<>();
        if(users!=null && !users.isEmpty()){
               userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }
        Long curUserId = BaseContext.getUserId();

        List<PostVO> res = new ArrayList<>();
        for (Post post : posts) {
            PostVO curRes = new PostVO();
            BeanUtils.copyProperties(post, curRes);
            Long count = stringRedisTemplate.opsForSet().size(POST_LIKE_KEY+post.getId());
            curRes.setLikeCount(count!=null?count:0);

            if(curUserId!=null){
                Boolean liked = stringRedisTemplate.opsForSet()
                        .isMember(POST_LIKE_KEY+post.getId(),String.valueOf(curUserId) );
                curRes.setHasLiked(Boolean.TRUE.equals(liked));
            }else{
                curRes.setHasLiked(false);
            }

            User curUser = userMap.get(post.getUserId());
            if(curUser!=null){
                curRes.setUserId(post.getUserId());
                curRes.setUserName(curUser.getNickName());
                curRes.setUserAvatar(curUser.getAvatar());
            }
            res.add(curRes);
        }
        return res;
    }

    @Override
    public List<Post> findAllPosts() {
        return postMapper.selectList(null);
    }

    @Override
    public int createPost(Post post) {
        post.setCreateTime(LocalDateTime.now());
        post.setUserId(BaseContext.getUserId());
        return postMapper.insert(post);
    }

    @Override
    public int updatePost(Post post) {
        post.setUpdateTime(LocalDateTime.now());
        return postMapper.updateById(post);
    }

    @Override
    public int deletePost(Long id) {
        return postMapper.deleteById(id);
    }

    @Override
    public Post findPostById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void likePost(Long postId) {
        String key = POST_LIKE_KEY + postId;
        Long userId = BaseContext.getUserId();

        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(postId));
        if(Boolean.FALSE.equals(isMember)){
            stringRedisTemplate.opsForSet().add(key, String.valueOf(userId));
        } else {
            stringRedisTemplate.opsForSet().remove(key, String.valueOf(userId));
        }
    }

    @Override
    public Long getLikeCount(Long postId) {
        String key = POST_LIKE_KEY + postId;
        return stringRedisTemplate.opsForSet().size(key);
    }
}
