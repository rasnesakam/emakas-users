import {ResourcePermissionSliderForm} from "@components/SliderForms/ResourcePermission";
import {
    Table,
    TableBody,
    TableCaption,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from "@components/shadcn/ui/table.tsx";
import {useEffect, useState} from "react";
import {ResourcePermission} from "@models/Permissions.ts";
import {PermissionTargetType} from "@utils/enums/PermissionTargetType.ts";
import {getGrantedPermissions} from "@services/permissions";
import {useAuthContext} from "@contexts/AuthContext";

export function PermissionsPage() {
    const {auth} = useAuthContext();
    const [permissions, setPermissions] = useState<ResourcePermission[]>([])

    useEffect(() => {
        getGrantedPermissions(auth!).then(setPermissions);
    }, []);

    return <>

        <div>
            <div className="flex flex-row justify-between">
                <div>
                    Granted Permissions
                </div>
                <div className="flex flex-row justify-end">
                    <ResourcePermissionSliderForm title={"Add New Resource Permission"}/>
                </div>
            </div>
            <Table>
                <TableCaption>Granted Permissions</TableCaption>
                <TableHeader>
                    <TableRow>
                        <TableHead>Resource Name</TableHead>
                        <TableHead>Subject</TableHead>
                        <TableHead>Subject Type</TableHead>
                        <TableHead>Access Modifier</TableHead>
                        <TableHead>Permission Scope</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {permissions.map((item, index) => (<TableRow key={`permission-${index}`}>
                        <TableCell>{item.resource.name}</TableCell>
                        <TableCell>{
                            item.permissionTargetType === PermissionTargetType.USER ? item.user.full_name :
                                item.permissionTargetType === PermissionTargetType.TEAM ? item.team.name :
                                    item.permissionTargetType === PermissionTargetType.APP ? item.application.name : ""
                        }</TableCell>
                        <TableCell>{item.permissionTargetType}</TableCell>
                        <TableCell>{item.accessModifier}</TableCell>
                        <TableCell>{item.permissionScope}</TableCell>
                    </TableRow>))}
                </TableBody>
            </Table>
        </div>

    </>
}