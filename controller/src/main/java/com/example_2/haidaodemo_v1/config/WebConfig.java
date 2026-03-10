package com.example_2.haidaodemo_v1.config;

import com.example_2.haidaodemo_v1.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/**/*.html",  // 放行所有 HTML 页面 (包括 login.html)
                        "/**/*.css",   // 放行样式表
                        "/**/*.js",    // 放行脚本
                        "/favicon.ico" // 放行小图标
                );
    }
}
