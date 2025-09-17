import {ResourcePermissionSliderForm} from "@components/SliderForms/ResourcePermission";
import {Table, TableBody, TableCaption, TableHead, TableHeader, TableRow} from "@components/shadcn/ui/table.tsx";

export function PermissionsPage() {

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
                        <TableHead>Permission Type</TableHead>
                        <TableHead>Permission Scope</TableHead>
                        <TableHead>Subject</TableHead>
                        <TableHead>Subject Type</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody></TableBody>
            </Table>
        </div>

    </>
}