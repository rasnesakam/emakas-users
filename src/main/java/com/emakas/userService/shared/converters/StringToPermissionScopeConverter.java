package com.emakas.userService.shared.converters;

import com.emakas.userService.shared.enums.PermissionScope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StringToPermissionScopeConverter implements Converter<String, Optional<PermissionScope>> {
    @Override
    public Optional<PermissionScope> convert(String source) {
        try {
            return Optional.of(PermissionScope.valueOf(source.toUpperCase()));
        }catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
