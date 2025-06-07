package com.emakas.userService.shared.expressionHandlers;

import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Autowired
    public CustomMethodSecurityExpressionHandler(TokenPermissionEvaluator tokenPermissionEvaluator) {
        this.setPermissionEvaluator(tokenPermissionEvaluator);
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        EvaluationContext context = super.createEvaluationContext(authentication, mi);
        context.setVariable("RSC_TEAMS", "iam.emakas.net/teams");
        return context;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        SecurityExpressionRoot root = (SecurityExpressionRoot) super.createSecurityExpressionRoot(authentication, invocation);

        return super.createSecurityExpressionRoot(authentication, invocation);
    }
}
