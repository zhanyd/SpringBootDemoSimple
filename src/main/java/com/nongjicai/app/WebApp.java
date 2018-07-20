package com.nongjicai.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nongjicai.app.mapper")
public class WebApp {
	public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }
}
