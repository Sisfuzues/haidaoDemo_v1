package com.example_2.haidaodemo_v1.service;

import com.example_2.haidaodemo_v1.pojo.User;

public interface UserService {
    /**
     * 注册
     */
    void register(User user);

    /**
     * 登录
     *
     * @return
     */
    User login(String username, String password);

    /**
     * 查询用户
     */
    User getUserByUsername(String username);

    /**
     * 按照传入的Pojo更改数据库内容。
     * @param user
     * @return
     */
    boolean updateById(User user);
}
