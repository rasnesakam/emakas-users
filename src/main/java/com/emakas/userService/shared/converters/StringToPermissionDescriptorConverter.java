package com.emakas.userService.shared.converters;

import com.emakas.userService.shared.data.PermissionDescriptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StringToPermissionDescriptorConverter implements Converter<String, PermissionDescriptor> {
    private final StringToAccessModifierConverter stringToAccessModifierConverter;
    private final StringToPermissionScopeConverter stringToPermissionScopeConverter;

    @Autowired
    public StringToPermissionDescriptorConverter(StringToAccessModifierConverter stringToAccessModifierConverter, StringToPermissionScopeConverter stringToPermissionScopeConverter) {
        this.stringToAccessModifierConverter = stringToAccessModifierConverter;
        this.stringToPermissionScopeConverter = stringToPermissionScopeConverter;
    }

    @Override
    public PermissionDescriptor convert(@NotNull String source) {
        PermissionDescriptor permissionDescriptor = new PermissionDescriptor();
        Pattern scopeAndModifierPattern = Pattern.compile("^(.*):(.*)$");
        Matcher patternMatcher = scopeAndModifierPattern.matcher(source);
        if (patternMatcher.matches()){
            permissionDescriptor.setPermissionScope(stringToPermissionScopeConverter.convert(patternMatcher.group(1)));
            permissionDescriptor.setAccessModifier(stringToAccessModifierConverter.convert(patternMatcher.group(2)));
        }
        else {
            permissionDescriptor.setAccessModifier(stringToAccessModifierConverter.convert(source));
        }
        return permissionDescriptor;
    }
}
