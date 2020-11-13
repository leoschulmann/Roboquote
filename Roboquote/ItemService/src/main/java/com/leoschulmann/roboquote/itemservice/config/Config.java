package com.leoschulmann.roboquote.itemservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@ComponentScan ("com.leoschulmann.roboquote.itemservice")
@EnableWebMvc
public class Config implements WebMvcConfigurer {
}

