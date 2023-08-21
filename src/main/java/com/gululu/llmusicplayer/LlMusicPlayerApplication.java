package com.gululu.llmusicplayer;

import com.gululu.llmusicplayer.util.BeanUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 开启定时任务
@MapperScan("com.gululu.llmusicplayer.mapper")
public class LlMusicPlayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlMusicPlayerApplication.class, args);
    }
    @Bean
    public BeanUtil beanUtil() {
        return new BeanUtil();
    }

}
