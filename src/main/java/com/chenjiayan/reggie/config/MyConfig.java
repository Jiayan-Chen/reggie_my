package com.chenjiayan.reggie.config;

import com.chenjiayan.reggie.common.BaseContext;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class MyConfig {

    @Bean
    public KeyGenerator myKeyGenerator(){
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... params) {
                String key = o.getClass().getSimpleName()+"."+method.getName()+ BaseContext.getCurrentId() + Arrays.asList(params);
                return key;
            }
        };
    }

}