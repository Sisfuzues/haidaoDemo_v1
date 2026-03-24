package com.example_2.haidaodemo_v1.service;

import com.example_2.haidaodemo_v1.VO.UserVO;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.pojo.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
     * 通过
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  
     * @return 
     * @author Sisfuzues
     * @date 2026/3/11 10:12
     */ 
    boolean updateById(User user);

    /**
     *  给指定邮箱发送验证code
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  email 字符串类型
     * @return Result
     * @author Sisfuzues
     * @date 2026/3/11 11:04
     */
    Result<String> sendByEmail(String email);

    /**
     *  根据邮箱验证码登录
     * <hr/>
     * 🧩 逻辑：
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param
     * @return  Result 成功返回对应用户的id  失败返回空Result
     * @author Sisfuzues
     * @date 2026/3/11 11:07
     */
    Result<String> loginByEmail(String email, String code);

    /**
     * 根据id查询用户
     * <hr/>
     * 🧩 逻辑：根据id查用户
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  id   String 类型的
     * @return 能查到返回User类，查不到返回 Null
     * @author Sisfuzues
     * @date 2026/3/12 20:43
     */
    User getUserById(String id);

    /**
     * 更新密码
     * <hr/>
     * 🧩 逻辑：根据用户id和新旧密码更改id
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param   id,  oldPassword,  newPassword
     * @return Boolean
     * @author Sisfuzues
     * @date 2026/3/15 23:13
     */
    Boolean updatePassword(Integer id, String oldPassword, String newPassword);

    List<User> listByIds(List<String> ids);

    UserVO getUserFromCache(Long id);

    void updateUserName(String newName, String oldName);
}
