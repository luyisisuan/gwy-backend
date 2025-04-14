package com.example.gwy_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // <<< 导入

@SpringBootApplication
@EnableAsync // <<< 添加注解以启用异步事件处理
public class GwyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwyBackendApplication.class, args);
	}

}