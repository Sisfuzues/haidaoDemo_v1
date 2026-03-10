package com.example_2.haidaodemo_v1.pojo;

import lombok.Data;
import java.sql.Date;

@Data
public class post {
    private Integer id;
    private String title;
    private String content;
    private String category;
    private Date createTime;
}
