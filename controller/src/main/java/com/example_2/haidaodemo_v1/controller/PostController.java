package com.example_2.haidaodemo_v1.controller;


import com.example_2.haidaodemo_v1.constant.Code;
import com.example_2.haidaodemo_v1.pojo.Post;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin
public class PostController {
    @Autowired
    PostService postService;

    @GetMapping
    public Result<List<Post>> getPost() {
        return Result.success(postService.findAllPosts());
    }

    @PostMapping
    public Result<Post> createPost(@RequestBody Post post) {
        postService.createPost(post);
        return Result.success(post);
    }

    @DeleteMapping("/{id}")
    public Result<String> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return Result.success("删除成功。") ;
    }

    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Integer id, @RequestBody Post post) {
        post.setId(id);
        postService.updatePost(post);
        return Result.success(post);
    }

    @GetMapping("/{id}")
    public Result<Post> getPostById(@PathVariable Integer id) {
        Post post = postService.findPostById(id);
        return post != null ? Result.success(post) : Result.error("未找到文章");
    }
}
