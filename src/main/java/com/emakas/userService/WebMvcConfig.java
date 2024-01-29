package com.emakas.userService;

import com.emakas.userService.shared.StringToScopeArrayConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringToScopeArrayConverter stringToScopeArrayConverter;

    @Autowired
    public WebMvcConfig(StringToScopeArrayConverter stringToScopeArrayConverter) {
        this.stringToScopeArrayConverter = stringToScopeArrayConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToScopeArrayConverter);
    }
}
