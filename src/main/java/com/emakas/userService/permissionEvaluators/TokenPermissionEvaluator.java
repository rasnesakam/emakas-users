package com.emakas.userService.permissionEvaluators;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.model.Token;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.shared.converters.StringToAccessModifierConverter;
import com.emakas.userService.shared.converters.StringToResourcePermissionConverter;
import com.emakas.userService.shared.enums.AccessModifier;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class TokenPermissionEvaluator implements PermissionEvaluator {
    private final ResourcePermissionService resourcePermissionService;
    private final StringToResourcePermissionConverter stringToResourcePermissionConverter;
    private final StringToAccessModifierConverter stringToAccessModifierConverter;

    @Autowired
    public TokenPermissionEvaluator(ResourcePermissionService resourcePermissionService, StringToResourcePermissionConverter stringToResourcePermissionConverter, StringToAccessModifierConverter stringToAccessModifierConverter) {
        this.resourcePermissionService = resourcePermissionService;
        this.stringToResourcePermissionConverter = stringToResourcePermissionConverter;
        this.stringToAccessModifierConverter = stringToAccessModifierConverter;
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
            Token token = jwtAuthentication.getUserToken();
            Optional<AccessModifier> requestedAccessModifierOptional = stringToAccessModifierConverter.convert(permission.toString());
            if (requestedAccessModifierOptional.isEmpty() || !authentication.isAuthenticated())
                return false;
            AccessModifier requestedAccessModifier = requestedAccessModifierOptional.get();
            String requestedResourceUri = targetDomainObject.toString();
            Stream<ResourcePermission> resourcePermissions = getResourcePermissionsFromToken(token, targetDomainObject);
            return resourcePermissions.anyMatch(rp -> resourcePermissionService.hasPermissionFor(rp, requestedResourceUri, requestedAccessModifier));
        }
        return false;
    }

    private Stream<ResourcePermission> getResourcePermissionsFromToken(@NotNull Token token, Object targetDomainObject) {
        return token.getScope().stream()
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
