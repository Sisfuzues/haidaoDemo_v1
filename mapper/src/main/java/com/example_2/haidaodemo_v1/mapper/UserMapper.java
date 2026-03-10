package com.example_2.haidaodemo_v1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example_2.haidaodemo_v1.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
