package com.example_2.haidaodemo_v1.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example_2.haidaodemo_v1.VO.UserVO;
import com.example_2.haidaodemo_v1.common.Utils.RedisUtils;
import com.example_2.haidaodemo_v1.mapper.UserMapper;
import com.example_2.haidaodemo_v1.pojo.Result;
import com.example_2.haidaodemo_v1.pojo.User;
import com.example_2.haidaodemo_v1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.example_2.haidaodemo_v1.constant.RedisConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public final UserMapper userMapper;
    public final StringRedisTemplate stringRedisTemplate;
    public final RedisUtils redisUtils;

    private final BCryptPasswordEncoder encoder
            = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        stringRedisTemplate.delete(USER_CACHE_KEY+user.getId());
        return userMapper.update(null,wrapper)>0;
    }

    @Override
    public Result<String> sendByEmail(String email) {
        if(!Validator.isEmail(email)){
            return Result.error("输入了错误的邮箱!")  ;
        }

        Boolean isLocked = stringRedisTemplate.opsForValue().setIfAbsent(
                LOGIN_LOCK_KEY+email,
                "1",
                60,
                TimeUnit.SECONDS);
        if(Boolean.FALSE.equals(isLocked)){
            return Result.error("请求过于频繁，请稍后再试。");
        }

        String code = RandomUtil.randomNumbers(6);

        redisUtils.set(LOGIN_CODE_KEY+email,
                code,LOGIN_CODE_TTL,TimeUnit.SECONDS);

        // TODO 模拟发送成功
        System.out.println("发送成功，验证码为："+code);
        return Result.success("成功发送!");
    }

    @Override
    public Result<String> loginByEmail(String email, String code) {
        if(!Validator.isEmail(email)){
            return Result.error("邮箱格式错误。");
        }

        String codeKey = LOGIN_CODE_KEY+email;
        String redisCode = stringRedisTemplate.opsForValue().get(codeKey);

        if(!StringUtils.equals(redisCode,code)){
            return Result.error("验证码错误");
        }

        stringRedisTemplate.delete(codeKey);
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));

        if(user==null){
            user =  new User();
            user.setEmail(email);
            user.setUsername("新用户"+RandomUtil.randomNumbers(12));
            user.setNickName("新用户"+RandomUtil.randomNumbers(12));
            userMapper.insert(user);
        }

        return Result.success(user.getId().toString());
    }

    @Override
    public User getUserById(String id) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
    }

    @Override
    public Boolean updatePassword(Integer id, String oldPassword, String newPassword) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
        if(user==null){
            return false;
        }
        if(!encoder.matches(oldPassword,user.getPassword())){
            return false;
        }
        user.setPassword(encoder.encode(newPassword));
        userMapper.updateById(user);
        return true;
    }

    @Override
    public List<User> listByIds(List<String> ids) {
        return userMapper.selectBatchIds(ids);
    }

    @Override
    public UserVO getUserFromCache(Long id) {
        String key = USER_CACHE_KEY+id;
        try{
            String json = stringRedisTemplate.opsForValue().get(key);

            if(json!=null && !json.isEmpty()){
                return objectMapper.readValue(json,UserVO.class);
            }

            User user = userMapper
                    .selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));

            UserVO resUserVO = new UserVO();
            BeanUtils.copyProperties(user,resUserVO);

            stringRedisTemplate.opsForValue()
                    .set(key,objectMapper.writeValueAsString(resUserVO),
                            USER_CACHE_TIME,TimeUnit.MINUTES);
            return resUserVO;
        }catch (Exception e){
            log.error("写入Redis失败。错误原因{}",e.getMessage());
            UserVO resUserVO = new UserVO();
            User curuser = userMapper.
                    selectOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
            BeanUtils.copyProperties(curuser,resUserVO);
            return resUserVO;
        }
    }

    @Override
    public void updateUserName(String newName, String oldName) {
        if(newName == null || newName.isEmpty()){
            throw new RuntimeException("新帐号名字不能为空");
        }
        if(newName.length()<6 || newName.length()>50){
            throw new RuntimeException("输入的新帐号名字长度不符合，请保证在6至50字符。");
        }
        Boolean isMem = stringRedisTemplate.opsForSet().isMember(CHANGE_USERNAME_KEY, oldName);
        if(Boolean.TRUE.equals(isMem)){
            throw new RuntimeException("代号更新过于频繁，请冷静 5 分钟");
        }

        User find = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, newName));
        if(find!=null){
            throw new RuntimeException("您的新帐号名与其他用户重合，请重新选择。");
        }
        User get =  userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, oldName));
        get.setUsername(newName);
        userMapper.updateById(get);

        stringRedisTemplate.opsForSet().add(CHANGE_USERNAME_KEY,oldName);
        stringRedisTemplate.expire(CHANGE_USERNAME_KEY,CHANGE_USERNAME_TIME,TimeUnit.MINUTES);
    }
}
