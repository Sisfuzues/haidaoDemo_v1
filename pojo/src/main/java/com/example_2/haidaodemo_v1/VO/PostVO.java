package com.example_2.haidaodemo_v1.VO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostVO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createTime;

    private Long userId;
    private String userName;
    private String userAvatar;

    private Long likeCount;
    private boolean hasLiked;
}
