package com.example_2.haidaodemo_v1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import com.example_2.haidaodemo_v1.pojo.Post;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
