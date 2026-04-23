import {Resource} from "@models/Resource.ts";
import {User} from "@models/User.ts";
import {Team} from "@models/Team.ts";
import {Application} from "@models/Application.ts";
import {PermissionTargetType} from "@utils/enums/PermissionTargetType.ts";
import {PermissionScope} from "@utils/enums/PermissionScope.ts";
import {AccessModifier} from "@utils/enums/AccessModifier.ts";

export interface ResourcePermission {
    id?: string;
    resource: Resource;
    user: User;
    team: Team;
    application: Application;
    permissionTargetType: PermissionTargetType,
    permissionScope: PermissionScope,
    accessModifier: AccessModifier
}