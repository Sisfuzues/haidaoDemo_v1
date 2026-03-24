package com.example_2.haidaodemo_v1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example_2.haidaodemo_v1.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
