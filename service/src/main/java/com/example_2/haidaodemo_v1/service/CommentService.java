package com.example_2.haidaodemo_v1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example_2.haidaodemo_v1.VO.CommentVO;
import com.example_2.haidaodemo_v1.pojo.Comment;
import java.util.List;

public interface CommentService extends IService<Comment> {
    List<CommentVO> getCommentsByPostId(Long postId,String sort);
    void likeComment(Long commentId);
    Long countLikes(Long commentId);
}
