package com.example_2.haidaodemo_v1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example_2.haidaodemo_v1.VO.PostVO;
import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.constant.Code;
import com.example_2.haidaodemo_v1.pojo.Post;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.service.PostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin
public class PostController {
    @Autowired
    PostService postService;

    @GetMapping("/list")
    public Result<List<PostVO>> getPost() {
        return Result.success(postService.findAll());
    }

    @PostMapping("/add")
    public Result<Post> createPost(@RequestBody Post post) {
        Long id = BaseContext.getUserId();
        post.setUserId(id);
        postService.save(post);
        return Result.success(post);
    }

    @DeleteMapping("/{id}")
    public Result<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success("删除成功。") ;
    }

    @PutMapping
    public Result<Post> updatePost(@RequestBody Post post) {
        postService.updatePost(post);
        return Result.success(post);
    }

    @GetMapping("/{id}")
    public Result<PostVO> getPostById(@PathVariable Long id) {
        PostVO post = postService.findById(Long.valueOf(id));
        return post != null ? Result.success(post) : Result.error("未找到文章");
    }

    @GetMapping("/author/{id}")
    public Result<List<PostVO>> getPostByAuthorId(@PathVariable Long id) {
        List<Post> res = postService.lambdaQuery().eq(Post::getUserId, id).list();
        List<PostVO> postVOList = new ArrayList<>();
        postVOList = res.stream().map(
                e -> {
                    PostVO postVO = new PostVO();
                    BeanUtils.copyProperties(e, postVO);
                    return  postVO;
                }
        ).toList();
        if (postVOList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        return Result.success(postVOList);
    }

    @PostMapping("/like/{postId}")
    public Result<String> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return Result.success("操作成功");
    }
}
