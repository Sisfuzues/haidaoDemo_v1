package com.example_2.haidaodemo_v1.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String fromNickName;
    private String fromAvatar;
    private String content;
    private Long parentId;
    private LocalDateTime createTime;
}
