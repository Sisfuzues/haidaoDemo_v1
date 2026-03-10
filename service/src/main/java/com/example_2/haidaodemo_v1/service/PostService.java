package com.example_2.haidaodemo_v1.service;

import com.example_2.haidaodemo_v1.pojo.Post;
import java.util.List;

public interface PostService {
    List<Post> findAllPosts();
    int createPost(Post post);
    int updatePost(Post post);
    int deletePost(Integer id);
    Post findPostById(Integer id);
}
