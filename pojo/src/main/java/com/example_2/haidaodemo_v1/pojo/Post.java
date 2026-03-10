package com.example_2.haidaodemo_v1.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Integer id;
    private String title;
    private String content;
    private String category;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String imageUrl;
    private Integer status;
    private Integer is_delete;
}
