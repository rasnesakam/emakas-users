import {ResourcePermission} from "@models/Permissions.ts";
import {AccessModifier} from "@utils/enums/AccessModifier.ts";
import {PermissionScope} from "@utils/enums/PermissionScope.ts";
import {PermissionTargetType} from "@utils/enums/PermissionTargetType.ts";
import {DeepPartial} from "@utils/utlis.ts";
import {Authentication} from "@models/Auth.ts";
import {invokeGetRequest, invokePostRequest} from "@services/core";

export function getFromFormData(formData: FormData): DeepPartial<ResourcePermission> {
    const accessModifier = formData.get("access-modifier")!.toString();
    const permissionScope = formData.get("permission-scope")!.toString();
    const permissionTargetType = formData.get("permission-target")!.toString();
    const appId = formData.get("application")?.toString() ?? undefined;
    const teamId = formData.get("team")?.toString() ?? undefined;
    const userId = formData.get("user")?.toString() ?? undefined;
    const resourceId = formData.get("resource")!.toString();
    return {
        accessModifier: accessModifier as AccessModifier,
        application: appId ? {client_id: appId} : undefined,
        permissionScope: permissionScope as PermissionScope,
        permissionTargetType: permissionTargetType as PermissionTargetType,
        team: teamId ? {id: teamId} : undefined,
        user: userId ? {id: userId} : undefined,
        resource: {id: resourceId}
    }
}

export async function assignPermission(resourcePermission: DeepPartial<ResourcePermission>, auth: Authentication) {
    return invokePostRequest("/api/resource-permissions/assign",undefined, resourcePermission, auth.access_token);
}

export async function getGrantedPermissions(auth: Authentication): Promise<ResourcePermission[]> {
    return invokeGetRequest("/api/resource-permissions/all", undefined, auth.access_token);
}