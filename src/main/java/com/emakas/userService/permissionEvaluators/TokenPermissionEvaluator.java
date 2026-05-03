package com.emakas.userService.permissionEvaluators;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.model.Token;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.shared.converters.StringToAccessModifierConverter;
import com.emakas.userService.shared.converters.StringToPermissionDescriptorConverter;
import com.emakas.userService.shared.converters.StringToResourcePermissionConverter;
import com.emakas.userService.shared.data.PermissionDescriptor;
import com.emakas.userService.shared.enums.AccessModifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class TokenPermissionEvaluator implements PermissionEvaluator {
    private final ResourcePermissionService resourcePermissionService;
    private final StringToResourcePermissionConverter stringToResourcePermissionConverter;
    private final StringToAccessModifierConverter stringToAccessModifierConverter;
    private final StringToPermissionDescriptorConverter stringToPermissionDescriptorConverter;

    @Autowired
    public TokenPermissionEvaluator(ResourcePermissionService resourcePermissionService, StringToResourcePermissionConverter stringToResourcePermissionConverter, StringToAccessModifierConverter stringToAccessModifierConverter, StringToPermissionDescriptorConverter stringToPermissionDescriptorConverter) {
        this.resourcePermissionService = resourcePermissionService;
        this.stringToResourcePermissionConverter = stringToResourcePermissionConverter;
        this.stringToAccessModifierConverter = stringToAccessModifierConverter;
        this.stringToPermissionDescriptorConverter = stringToPermissionDescriptorConverter;
    }

    /**
     * <p>
     *     Evaluates jwt token. This must be done on token level. No db fetches.
     *     Evaluation must be quick and fast.
     * </p>
     * @param authentication represents the user in question. Should not be null.
     * @param targetDomainObject the domain object for which permissions should be
     * checked. In this case, it is corresponding to uri of the resource.
     * @param permission a representation of the permission object as supplied by the
     * expression system. Not null. In this case, it is corresponding to access modifier of permission.
     * @return <code>true</code> if has permission, else <code>false</code>
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication instanceof JwtAuthentication jwtAuthentication){
            UserPrincipal principal = (UserPrincipal) jwtAuthentication.getPrincipal();
            PermissionDescriptor permissionDescriptor = stringToPermissionDescriptorConverter.convert(permission.toString());
            String requestedResourceUri = targetDomainObject.toString();
            Stream<ResourcePermission> resourcePermissions = getResourcePermissionsFromToken(principal.getAuthorities().toArray(new String[0]), targetDomainObject);
            return resourcePermissions.anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, requestedResourceUri, permissionDescriptor));
        }
        return false;
    }

    private Stream<ResourcePermission> getResourcePermissionsFromToken(String[] scopes, Object targetDomainObject) {
        return Arrays.stream(scopes)
                .map(stringToResourcePermissionConverter::convert)
                .filter(Objects::nonNull).filter(Optional::isPresent)
                .map(Optional::get)
                .filter(rp -> rp.getResource().getUri().equals(targetDomainObject));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
