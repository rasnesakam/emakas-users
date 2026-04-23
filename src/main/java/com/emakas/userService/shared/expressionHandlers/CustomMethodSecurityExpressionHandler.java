package com.emakas.userService.shared.expressionHandlers;

import com.emakas.userService.model.Resource;
import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final Map<String, Resource> resourceMap;

    @Autowired
    public CustomMethodSecurityExpressionHandler(TokenPermissionEvaluator tokenPermissionEvaluator, Map<String, Resource> resourceMap) {
        this.resourceMap = resourceMap;
        this.setPermissionEvaluator(tokenPermissionEvaluator);
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        EvaluationContext context = super.createEvaluationContext(authentication, mi);
        resourceMap.forEach((key, value) -> context.setVariable(key,value.getUri()));
        return context;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        SecurityExpressionRoot root = (SecurityExpressionRoot) super.createSecurityExpressionRoot(authentication, invocation);

        return super.createSecurityExpressionRoot(authentication, invocation);
    }
}
