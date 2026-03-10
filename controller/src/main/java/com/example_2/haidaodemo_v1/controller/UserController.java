package com.example_2.haidaodemo_v1.controller;

import com.example_2.haidaodemo_v1.common.JwtUtils;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.pojo.User;
import com.example_2.haidaodemo_v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
        System.out.println("成功进入登录逻辑！用户名为：" + user.getUsername());
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            Map<String, Object> claims = Map.of(
                    "id" , loginUser.getId(),
                    "username" , loginUser.getUsername()
            );

            String token = jwtUtils.createToken(claims);
            return Result.success(token);
        }
        return Result.error("代号或密码有误，登录失败。");
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("恭喜，注册成功！");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            Map<String,Object> claims = jwtUtils.parseToken(token);

            User dbUser = userService
                    .getUserByUsername(claims.get("username").toString());
            if(dbUser==null){
                return Result.error("查无此人，请重新登录");
            }
            dbUser.setPassword(null);
            return Result.success(dbUser);
        } catch (Exception e) {
            return Result.error("身份令牌已经失效，请重新登录。");
        }
    }

    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody User user,
                                         @RequestHeader("Authorization") String token) {
        Map<String,Object> claims = jwtUtils.parseToken(token);
        Integer curId = claims.get("id")==null?null:Integer.parseInt(claims.get("id").toString());
        user.setId(curId);

        boolean success = userService.updateById(user);
        return success?Result.success("成功更新"):Result.error("更新失败");
    }
}
