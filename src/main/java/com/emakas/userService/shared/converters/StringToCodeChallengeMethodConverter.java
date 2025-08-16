package com.emakas.userService.shared.converters;

import com.emakas.userService.shared.enums.CodeChallengeMethod;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class StringToCodeChallengeMethodConverter implements Converter<String, CodeChallengeMethod> {
    @Override
    public CodeChallengeMethod convert(@NotNull String source) {
        return Arrays.stream(CodeChallengeMethod.values()).filter(method -> method.getNormalizedName().equals(source)).findFirst().orElse(CodeChallengeMethod.UNKNOWN);
    }
}
