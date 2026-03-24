package com.example_2.haidaodemo_v1.controller;

import com.example_2.haidaodemo_v1.VO.CommentVO;
import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.pojo.Comment;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public Result<String> addComment(@RequestBody Comment comment){
        Long userId = BaseContext.getUserId();
        if(userId==null){
            return Result.error("未登录或身份已过期");
        }
        comment.setUserId(userId);
        commentService.save(comment);

        return Result.success("评论发表成功");
    }

    @GetMapping("/list/{postId}")
    public Result<List<CommentVO>> listComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "time") String sort){
        List<CommentVO> res = commentService.getCommentsByPostId(postId,sort);
        return Result.success(res);
    }

    @PostMapping("/like")
    public Result<String> likeComment(@RequestParam Long commentId){
        Comment comment = commentService.getById(commentId);
        if(comment==null){
            System.err.println("查找不到当前的评论");
            return Result.error("查找不到当前的评论 评论Id:"+commentId);
        }
        commentService.likeComment(commentId);
        return Result.success("点赞成功");
    }

    @DeleteMapping
    public Result<String> deleteComment(@RequestParam Long commentId){
        Long userId = BaseContext.getUserId();
        if(userId==null){
            return Result.error("登录过期");
        }
        Comment curComment = commentService.getById(commentId);
        if(curComment==null){
            System.err.println("查找不到当前的评论");
            return Result.error("查找不到当前的评论 评论Id:"+commentId);
        }
        if(!userId.equals(curComment.getUserId())){
            return Result.error("您不是当前评论的作者，删除失败。");
        }
        commentService.removeById(commentId);
        return Result.success("删除成功");
    }
}
