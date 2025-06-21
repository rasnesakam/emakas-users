package com.emakas.userService.shared.converters;

import com.emakas.userService.shared.enums.AccessModifier;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;

public class StringToAccessModifierConverter implements Converter<String, Optional<AccessModifier>> {
    @Override
    @NotNull
    public Optional<AccessModifier> convert(@NotNull String source) {
        try {
            return Optional.of(AccessModifier.valueOf(source.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
