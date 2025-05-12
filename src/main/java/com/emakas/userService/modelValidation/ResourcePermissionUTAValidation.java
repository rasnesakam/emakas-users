package com.emakas.userService.modelValidation;

import com.emakas.userService.modelValidators.ResourcePermissionUTAValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ResourcePermissionUTAValidator.class)
public @interface ResourcePermissionUTAValidation {
    String message() default "User, team, and application values can't be null or not null all together! Please specify only one of them and leave others null";
}
