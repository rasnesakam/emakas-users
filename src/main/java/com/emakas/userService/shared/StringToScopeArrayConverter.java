package com.emakas.userService.shared;
import com.emakas.userService.shared.enums.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToScopeArrayConverter implements Converter<String, Scope[]> {

    @Override
    public Scope[] convert(String source) {
        if (source.isEmpty())
            return new Scope[0];
        String[] scopeStrings = source.split(",");
        Scope[] scopes = new Scope[scopeStrings.length];

        for (int i = 0; i < scopeStrings.length; i++) {
            scopes[i] = Scope.valueOf(scopeStrings[i].trim());
        }

        return scopes;
    }
}
