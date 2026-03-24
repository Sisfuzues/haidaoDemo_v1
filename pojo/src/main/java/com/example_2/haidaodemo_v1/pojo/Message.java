package com.example_2.haidaodemo_v1.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String content;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("from_user_id")
    private Long fromUserId;
    @TableField("to_user_id")
    private Long toUserId;
    @TableField("parent_id")
    private Long parentId;
    @TableField("is_read")
    private Boolean isRead;
}
