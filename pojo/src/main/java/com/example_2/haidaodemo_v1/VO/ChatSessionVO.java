package com.example_2.haidaodemo_v1.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatSessionVO {
    private Long targetId;
    private String fromNickName;
    private String fromAvatar;
    private String recentContent;
    private LocalDateTime recentTime;
    private Integer notRead;
}
