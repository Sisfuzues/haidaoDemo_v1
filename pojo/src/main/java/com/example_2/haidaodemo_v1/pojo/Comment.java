package com.example_2.haidaodemo_v1.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("post_id")
    private Long postId;
    private String content;
    @TableField("parent_id")
    private Long parentId;
    private int likes;
    @TableField("create_time")
    private LocalDateTime createTime;
}
