package com.emakas.userService.permissionEvaluators;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.mappers.UserPrincipalMapper;
import com.emakas.userService.model.AccessToken;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.Resource;
import com.emakas.userService.model.User;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.shared.converters.StringToAccessModifierConverter;
import com.emakas.userService.shared.converters.StringToPermissionDescriptorConverter;
import com.emakas.userService.shared.converters.StringToPermissionScopeConverter;
import com.emakas.userService.shared.converters.StringToResourcePermissionConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static com.emakas.userService.shared.enums.PrincipalType.APP;
import static com.emakas.userService.shared.enums.PrincipalType.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenPermissionEvaluatorTest {
    private final User user = new User();
    private final Application application = new Application();
    private final Resource reports = new Resource("Reports", "", "reports", null);
    private final Resource invoices = new Resource("Invoices", "", "invoices", null);
    private final TokenPermissionEvaluator evaluator = createEvaluator();

    @ParameterizedTest
    @CsvSource({
            "self:read, self:read, true",
            "self:read, self:write, false",
            "self:read, global:read, false",
            "global:read_write, self:write, true",
            "global:read_write, global:read, true"
    })
    void evaluatesUserPermissions(String grantedPermission, String requestedPermission, boolean expected) {
        AccessToken token = userToken(Set.of(grantedPermission + ":" + reports.getUri()));

        assertEquals(expected, evaluator.hasPermission(authenticate(token), reports.getUri(), requestedPermission));
    }

    @Test
    void deniesUserAccessWithoutResourcePermission() {
        AccessToken token = userToken(Set.of("openid"));

        assertFalse(evaluator.hasPermission(authenticate(token), reports.getUri(), "self:read"));
    }

    @Test
    void limitsApplicationAccessToItsGrantedResourceAndOperation() {
        AccessToken token = new AccessToken();
        token.setSub(application.getId().toString());
        token.setPrincipalType(APP);
        token.setClient(application);
        token.setScope(Set.of("global:read:" + invoices.getUri()));
        JwtAuthentication authentication = authenticate(token);

        assertTrue(evaluator.hasPermission(authentication, invoices.getUri(), "global:read"));
        assertFalse(evaluator.hasPermission(authentication, invoices.getUri(), "global:write"));
        assertFalse(evaluator.hasPermission(authentication, reports.getUri(), "global:read"));
    }

    @Test
    void deniesAnonymousAccess() {
        var authentication = new AnonymousAuthenticationToken(
                "test-key", "anonymous", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));

        assertFalse(evaluator.hasPermission(authentication, reports.getUri(), "self:read"));
    }

    private AccessToken userToken(Set<String> scopes) {
        AccessToken token = new AccessToken();
        token.setSub(user.getId().toString());
        token.setPrincipalType(USER);
        token.setClient(application);
        token.setScope(scopes);
        return token;
    }

    private static JwtAuthentication authenticate(AccessToken token) {
        return new JwtAuthentication(UserPrincipalMapper.getInstance().fromUserToken(token));
    }

    private static TokenPermissionEvaluator createEvaluator() {
        var accessConverter = new StringToAccessModifierConverter();
        var descriptorConverter = new StringToPermissionDescriptorConverter(
                accessConverter, new StringToPermissionScopeConverter());
        return new TokenPermissionEvaluator(new ResourcePermissionService(null, null, null, null, null),
                new StringToResourcePermissionConverter(), accessConverter, descriptorConverter);
    }
}
