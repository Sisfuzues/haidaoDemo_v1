package com.example_2.haidaodemo_v1.service.impl;

import com.example_2.haidaodemo_v1.mapper.PostMapper;
import com.example_2.haidaodemo_v1.pojo.Post;
import com.example_2.haidaodemo_v1.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Post> findAllPosts() {
        return postMapper.selectAll();
    }

    @Override
    public int createPost(Post post) {
        post.setCreateTime(LocalDateTime.now());
        return postMapper.insert(post);
    }

    @Override
    public int updatePost(Post post) {
        post.setUpdateTime(LocalDateTime.now());
        return postMapper.updatePost(post);
    }

    @Override
    public int deletePost(Integer id) {
        return postMapper.deletePost(id);
    }

    @Override
    public Post findPostById(Integer id) {
        return postMapper.findPostById(id);
    }
}
