package com.tenco.blog_v2.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // IoC
public class InterceptorConfig {

    @Bean // 빈으로 등록 처리 : 로그인 인터셉터를 빈으로 등록
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    // admin 인터셉터 등록
    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }

}
