package com.chenjiayan.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@ServletComponentScan
@Slf4j
@EnableTransactionManagement //开启事务管理
@EnableCaching
public class Application {
    public static void main(String[] args) {

        SpringApplication.run(Application.class);
        log.info("项目启动成功。。");
    }
}
