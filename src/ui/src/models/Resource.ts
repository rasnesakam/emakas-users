import {User} from "@models/User.ts";
import {Team} from "@models/Team.ts";
import {Application} from "@models/Application.ts";
import {PermissionTargetType} from "@utils/enums/PermissionTargetType.ts";
import {PermissionScope} from "@utils/enums/PermissionScope.ts";
import {AccessModifier} from "@utils/enums/AccessModifier.ts";


export interface ExternalResourceInfo {
    resourceName: string;
    client_id: string;
    redirectUri: string;
    audiences: string[];
    scopes: string[];
    state?: string;
}

export interface Resource {
    name: string;
    description: string;
    uri: string;
}

export interface ResourcePermission {
    resource: Resource;
    user: User;
    team: Team;
    application: Application;
    permissionTargetType: PermissionTargetType;
    permissionScope: PermissionScope;
    accessModifier: AccessModifier;
}