package com.emakas.userService.modelValidators;

import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.modelValidation.ResourcePermissionUTAValidation;
import com.emakas.userService.shared.enums.PermissionTargetType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ResourcePermissionUTAValidator implements ConstraintValidator<ResourcePermissionUTAValidation, ResourcePermission> {

    @Override
    public boolean isValid(ResourcePermission resourcePermission, ConstraintValidatorContext constraintValidatorContext) {
        boolean isAllEmpty = resourcePermission.getApplication() == null
                && resourcePermission.getTeam() == null
                && resourcePermission.getUser() == null;
        boolean isAllNonNull = resourcePermission.getApplication() != null
                && resourcePermission.getTeam() != null
                && resourcePermission.getUser() != null;
        boolean isCorrectlyMatched = (resourcePermission.getApplication() != null && resourcePermission.getPermissionTargetType() == PermissionTargetType.APP )
                || (resourcePermission.getTeam() != null && resourcePermission.getPermissionTargetType() == PermissionTargetType.TEAM)
                || (resourcePermission.getUser() != null && resourcePermission.getPermissionTargetType() == PermissionTargetType.USER);

        return !isAllEmpty && !isAllNonNull && isCorrectlyMatched;
    }
}
