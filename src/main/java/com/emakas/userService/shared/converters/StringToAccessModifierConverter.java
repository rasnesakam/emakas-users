package com.emakas.userService.shared.converters;

import com.emakas.userService.shared.enums.AccessModifier;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;

public class StringToAccessModifierConverter implements Converter<String, Optional<AccessModifier>> {
    @Override
    @NotNull
    public Optional<AccessModifier> convert(@NotNull String source) {
        if (source.equals(AccessModifier.READ.toString().toLowerCase()))
            return Optional.of(AccessModifier.READ);
        else if (source.equals(AccessModifier.WRITE.toString().toLowerCase()))
            return Optional.of(AccessModifier.WRITE);
        else if (source.equals(AccessModifier.READ_WRITE.toString().toLowerCase()))
            return Optional.of(AccessModifier.READ_WRITE);
        else
            return Optional.empty();
    }
}
