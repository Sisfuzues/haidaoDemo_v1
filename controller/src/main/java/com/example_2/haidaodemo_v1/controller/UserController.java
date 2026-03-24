package com.example_2.haidaodemo_v1.controller;

import com.example_2.haidaodemo_v1.common.DTO.UserRequest;
import com.example_2.haidaodemo_v1.common.Utils.AliyunOSSUtils;
import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.common.Utils.JwtUtils;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.pojo.User;
import com.example_2.haidaodemo_v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.example_2.haidaodemo_v1.constant.RedisConstants.CHANGE_USERNAME_KEY;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AliyunOSSUtils aliyunOSSUtils;

    @PostMapping("/login")
    public Result<String> login(@RequestBody User user) {
//        System.out.println("成功进入登录逻辑！用户名为：" + user.getUsername());
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            Map<String, Object> claims = Map.of(
                    "id" , loginUser.getId()
            );

            String token = jwtUtils.createToken(claims);
            return Result.success(token);
        }
        return Result.error("代号或密码有误，登录失败。");
    }

    @PostMapping("/login/email")
    public Result<String> loginByEmail(@RequestParam String email,@RequestParam String code) {
        Result<String> result = userService.loginByEmail(email, code);

        if(result.isSuccess()){
            String uId = result.getData();
            Map<String, Object> claims = Map.of(
                    "id" , uId
            );
            String token = jwtUtils.createToken(claims);
            return Result.success(token);
        }
        return result;
    }

    @PostMapping("/code")
    public Result<String> loginByCode(@RequestParam String email) {
        return userService.sendByEmail(email);
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
    public Result<User> getUserInfo() {
        Long userId = BaseContext.getUserId();
        User user = userService.getUserById(userId.toString());
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<String> updateUserNickName(@RequestBody UserRequest.UpdateProfile userRequest) {
        String nickName = userRequest.getNickname();
        Long userId = BaseContext.getUserId();
        User user = new User();
        user.setId(userId);
        user.setNickName(nickName);
        return userService.updateById(user)?Result.success("更新成功"):Result.error("更新失败");
    }

    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody UserRequest.UpdatePassword userRequest) {
        String oldPassword = userRequest.getOldPassword();
        String newPassword = userRequest.getNewPassword();
        Long userId = BaseContext.getUserId();
        return userService.updatePassword(Math.toIntExact(userId),oldPassword,newPassword)
                ?Result.success("更新成功"):Result.error("更新失败");
    }

    @PostMapping("/avatar")
    public Result<String> updateAvatar(MultipartFile avatar) {
        try{
            String avatarUrl = aliyunOSSUtils.upload(avatar);

            Long userId = BaseContext.getUserId();
            User user = new User();
            user.setId(userId);
            user.setAvatar(avatarUrl);
            userService.updateById(user);

            return Result.success(avatarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("云端存储失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(String.valueOf(id));
        if(user == null){
            return Result.error("查找失败。");
        }
        return Result.success(user);
    }

    @PutMapping("/username")
    public Result<String> updateUserName(@RequestBody Map<String, String> params) {

        userService.updateUserName(params.get("username"), params.get("oldName"));
        return Result.success("更改成功");
    }
}
