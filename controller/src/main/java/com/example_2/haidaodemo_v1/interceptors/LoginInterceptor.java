package com.example_2.haidaodemo_v1.interceptors;

import com.example_2.haidaodemo_v1.common.Utils.BaseContext;
import com.example_2.haidaodemo_v1.common.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        // 从请求头header中获得令牌，约定key是 Authorization
        String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty()){
            token = request.getHeader("token");
        }
        if(token == null || token.isEmpty()){
            token = request.getHeader("Token");
        }

        if(token == null || token.isEmpty()){
            System.out.println("🚨 拦截器阻截！非法路径: " + uri + " (原因: 未收到Token)");
            System.out.println("拦截器未收到任何形式的Token!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            Map<String, Object> claims = jwtUtils.parseToken(token);
            Object userId = claims.get("id");
            if(userId != null){
                BaseContext.setUserId(Long.valueOf(userId.toString()));
            }
            return true;
        } catch (Exception e) {
            System.out.println("拦截器解析失败!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
