package com.emakas.userService;

import com.emakas.userService.shared.converters.StringToAccessModifierConverter;
import com.emakas.userService.shared.converters.StringToResourcePermissionConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public StringToResourcePermissionConverter stringToResourcePermissionConverter() {
        return new StringToResourcePermissionConverter();
    }
    @Bean
    public StringToAccessModifierConverter stringToAccessModifierConverter(){
        return new StringToAccessModifierConverter();
    }
}
