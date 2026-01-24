package com.example.SP26SE025.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List; 

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    // LỖI CŨ CỦA BẠN: public void configureMessageConverters(HttpMessageConverter<?> converters)
    // SỬA THÀNH: List<HttpMessageConverter<?>> (Thêm chữ List vào)
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(stringConverter);
    }
}