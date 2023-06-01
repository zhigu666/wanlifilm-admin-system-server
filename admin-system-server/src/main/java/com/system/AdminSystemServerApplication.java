package com.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.system.mapper")
public class AdminSystemServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminSystemServerApplication.class, args);
    }

}
