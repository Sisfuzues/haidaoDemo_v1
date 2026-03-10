package com.example_2.haidaodemo_v1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example_2.haidaodemo_v1.mapper.UserMapper;
import com.example_2.haidaodemo_v1.pojo.User;
import com.example_2.haidaodemo_v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder encoder
            = new BCryptPasswordEncoder();


    @Override
    public void register(User user) {
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername,user.getUsername()));
        if(existingUser!=null){
            throw new RuntimeException("该用户名已经被占用。");
        }
        String encodePassword = encoder.encode(user.getPassword());
        user.setNickName("默认用户_"+user.getUsername());
        user.setPassword(encodePassword);
        userMapper.insert(user);
        return ;
    }

    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        //        System.out.println("========== 密码比对现场 ==========");
        //        System.out.println("1. 前端传来的明文密码: [" + password + "]");
        //        System.out.println("2. 数据库查出的密文密码: [" + (user == null ? "查无此人" : user.getPassword()) + "]");
        //        System.out.println("==================================");
        //        // 直接用强硬手段测试这串密文到底能不能匹配 123456
        //        boolean isRealMatch = encoder.matches("123456", "$2a$10$K55bGjhl2G2I4GvOhucIU.pDlnNoQumGObdy3PfD.eX.DXvKUL4P2");
        //        System.out.println("====== 终极测谎仪结果: " + isRealMatch + " ======");
        if (user != null && encoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername,username));
    }

    @Override
    public boolean updateById(User user) {
        if(user.getId()==null){
            return false;
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId,user.getId());

        if(user.getNickName()!=null) wrapper.set(User::getNickName,user.getNickName());
        if(user.getAvatar()!=null) wrapper.set(User::getAvatar,user.getAvatar());
        return userMapper.update(null,wrapper)>0;
    }

}
