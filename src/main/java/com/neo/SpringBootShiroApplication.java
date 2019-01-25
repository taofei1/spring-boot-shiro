package com.neo;

import com.neo.config.CustomMultipartResolver;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartResolver;

@SpringBootApplication(exclude = {MultipartAutoConfiguration.class})
@EnableJpaAuditing
@EnableScheduling
@EnableCaching
@ServletComponentScan(basePackages = {"com.neo.druid"})
@MapperScan("com.neo.mapper")
@EnableAsync
public class SpringBootShiroApplication {
	//注入自定义的文件上传处理类
	@Bean(name = "multipartResolver")
	public MultipartResolver multipartResolver() {
		CustomMultipartResolver customMultipartResolver = new CustomMultipartResolver();
		return customMultipartResolver;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringBootShiroApplication.class, args);

	}
}
