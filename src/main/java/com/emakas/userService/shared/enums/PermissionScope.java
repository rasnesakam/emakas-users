package com.emakas.userService.shared.enums;

public enum PermissionScope {
    /**
     * <h1>PermissionScope.SELF</h1>
     * Represents that user can acces on their own resources
     */
    SELF,

    /**
     * <h1>PermissionScope.TEAM</h1>
     * Represents that user can acces on their own and team's resources
     */
    TEAM,

    /**
     * <h1>PermissionScope.ALL</h1>
     * Represents that user can acces on their own, team's and sub team's resources
     */
    ALL;
}
