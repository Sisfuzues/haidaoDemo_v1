package com.example_2.haidaodemo_v1.common.DTO;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String toUserId;
    private String content;
    private String parentId;
}
