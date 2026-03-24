package com.example_2.haidaodemo_v1.VO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private Long parentId;
    private Long userId;
    private String content;
    private Long likes;
    private Boolean isLiked;
    private LocalDateTime createTime;

    private String userNickname;
    private String userAvatar;
}
