package com.emakas.userService.shared.converters;
import com.emakas.userService.shared.enums.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StringToScopeArrayConverter implements Converter<String, Scope[]> {

    public static Scope convertStringToScope(String scope){
        return Scope.valueOf(scope);
    }

    @Override
    public Scope[] convert(String source) {
        if (source.isEmpty())
            return new Scope[0];
        return Arrays.stream(source.split(",")).map(StringToScopeArrayConverter::convertStringToScope).toArray(Scope[]::new);
    }
}
