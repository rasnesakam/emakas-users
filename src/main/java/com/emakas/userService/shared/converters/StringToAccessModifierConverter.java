package com.emakas.userService.shared.converters;

import com.emakas.userService.shared.enums.AccessModifier;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;

public class StringToAccessModifierConverter implements Converter<String, Optional<AccessModifier>> {
    @Override
    
    public Optional<AccessModifier> convert(String source) {
        try {
            return Optional.of(AccessModifier.valueOf(source.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
