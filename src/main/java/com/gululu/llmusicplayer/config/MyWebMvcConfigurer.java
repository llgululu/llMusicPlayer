package com.gululu.llmusicplayer.config;

import com.gululu.llmusicplayer.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class MyWebMvcConfigurer extends WebMvcConfigurationSupport {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        // 访问绝对路径 访问路径和 存放路径可以自定义，建议存放路径放到配置文件中
//        registry.addResourceHandler("/data/personImg/**").addResourceLocations("file:G:/loveDiary/personalImag/");
//        registry.addResourceHandler("/update/personImg/**").addResourceLocations("file:G:/loveDiary/images/");
//        registry.addResourceHandler("/update/albumCover/**").addResourceLocations("file:G:/loveDiary/albumCover/");
//        super.addResourceHandlers(registry);
//    }
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor()).addPathPatterns("/**").excludePathPatterns("/login/**");
        super.addInterceptors(registry);
    }
}
