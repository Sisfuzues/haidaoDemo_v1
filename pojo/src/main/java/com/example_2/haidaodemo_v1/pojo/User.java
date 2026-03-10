package com.example_2.haidaodemo_v1.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    @TableId(type = IdType.AUTO,value = "user_id")
    private Integer id;
    @TableField("user_name")
    private String username;
    @TableField("nick_name")
    private String nickName;
    private String password;
    private String email;
    @TableField("user_sex")
    private String sex;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("avater")
    private String avatar;
}
