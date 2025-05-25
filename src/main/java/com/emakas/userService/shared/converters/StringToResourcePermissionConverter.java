package com.emakas.userService.shared.converters;

import com.emakas.userService.model.Resource;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.enums.AccessModifier;
import com.emakas.userService.shared.enums.PermissionScope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class StringToResourcePermissionConverter implements Converter<String, Optional<ResourcePermission>> {

    public static final int INDEX_OF_PERMISSION_SCOPE = 0;
    public static final int INDEX_OF_ACCESS_MODIFIER = 1;
    public static final int INDEX_OF_RESOURCE_URI = 2;

    /**
     * <h1>StringToResourcePermissionConverter.convert(String source)</h1>
     * <p>String representation of resource permission</p>
     * <p>Format of string should be as follows</p>
     * <code>[permissionScope]:[accessModifier]:[resourceUri]</code>
     * @param source String representation of {@link ResourcePermission}
     * @return ResourcePermission object for the string
     * @author Ensar Makas
     */
    @Override
    public Optional<ResourcePermission> convert(String source) {
        ResourcePermission resourcePermission = new ResourcePermission();
        Resource resource = new Resource();
        String[] split = source.split(Constants.SEPARATOR);
        if (split.length == 3) {
            PermissionScope permissionScope = PermissionScope.valueOf(split[INDEX_OF_PERMISSION_SCOPE].toUpperCase(Locale.ROOT));
            AccessModifier accessModifier = AccessModifier.valueOf(split[INDEX_OF_ACCESS_MODIFIER].toUpperCase(Locale.ROOT));
            resource.setUri(split[INDEX_OF_RESOURCE_URI]);
            resourcePermission.setPermissionScope(permissionScope);
            resourcePermission.setAccessModifier(accessModifier);
            resourcePermission.setResource(resource);
            return Optional.of(resourcePermission);
        }
        return Optional.empty();
    }
}
