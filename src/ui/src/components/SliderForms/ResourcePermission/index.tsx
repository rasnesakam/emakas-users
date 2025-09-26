import {
    Sheet,
    SheetClose,
    SheetContent,
    SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle,
    SheetTrigger
} from "@components/shadcn/ui/sheet.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue
} from "@components/shadcn/ui/select.tsx";
import {getAllResources} from "@services/resources";
import {useAuthContext} from "@contexts/AuthContext";
import {FormEvent, useEffect, useState} from "react";
import {Resource} from "@models/Resource.ts";
import {PermissionTargetType} from "@utils/enums/PermissionTargetType.ts";
import {assignPermission, getFromFormData} from "@services/permissions";
import {Label} from "@components/shadcn/ui/label.tsx";
import {Team} from "@models/Team.ts";
import {getUserTeams} from "@services/teams";
import {Application} from "@models/Application.ts";
import {getApplications} from "@services/applications";
import {AccessModifier} from "@utils/enums/AccessModifier.ts";
import {PermissionScope} from "@utils/enums/PermissionScope.ts";

const PERMISSION_TYPES: {label: string, value: PermissionTargetType}[] = [
    {
        label: "Application",
        value: PermissionTargetType.APP,
    },
    {
        label: "User",
        value: PermissionTargetType.USER,
    },
    {
        label: "Team",
        value: PermissionTargetType.TEAM,
    }
]

export function ResourcePermissionSliderForm({title}: {title: string}) {
    const {auth} = useAuthContext();
    const [isFormOpen, setIsFormOpen] = useState<boolean>(false);
    const [permissionTarget, setPermissionTarget] = useState<PermissionTargetType | undefined>(undefined);

    const [resources, setResources] = useState<Resource[]>([])
    const [teams, setTeams] = useState<Team[]>([]);
    const [apps, setApps] = useState<Application[]>([])

    function onFormSubmit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const resourcePermission = getFromFormData(formData);
        console.log(resourcePermission);
        assignPermission(resourcePermission, auth!).then(() => {});
    }

    useEffect(() => {
        getAllResources(auth!).then(setResources);
        getUserTeams(auth!).then(setTeams);
        getApplications(auth!).then(setApps);
    }, []);

    return <Sheet open={isFormOpen} onOpenChange={setIsFormOpen}>
        <SheetTrigger>
            <Button>{title}</Button>
        </SheetTrigger>
        <SheetContent>
            <form onSubmit={onFormSubmit} className="h-full">
                <div className="flex flex-col justify-between h-full">
                    <div className="h-full">
                        <SheetHeader>
                            <SheetTitle>
                                Create New Resource Permission
                            </SheetTitle>
                            <SheetDescription>
                                Description
                            </SheetDescription>
                        </SheetHeader>

                        <div className="p-4">
                            <div className="my-4">
                                <Label>
                                    <span className="w-1/4">
                                        Resource
                                    </span>
                                    <Select name="resource">
                                        <SelectTrigger className="w-3/4">
                                            <SelectValue placeholder={"Select Resource"}/>
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Resources</SelectLabel>
                                                {resources.map((resource, index) => (<SelectItem value={resource.uri}
                                                                                                 key={`resource-select-${index}`}>{resource.name}</SelectItem>))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </Label>
                            </div>

                            <div className="my-4">
                                <Label>
                                    <span className="w-1/4">
                                        Permission
                                    </span>
                                    <Select name="access-modifier">
                                        <SelectTrigger className="w-3/4">
                                            <SelectValue placeholder={"Select Permission"}/>
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Permissions</SelectLabel>
                                                {Object.keys(AccessModifier).map((access_modifier, index) => (
                                                    <SelectItem value={access_modifier}
                                                                key={`resource-select-${index}`}>{access_modifier}</SelectItem>))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </Label>
                            </div>

                            <div className="my-4">
                                <Label>
                                    <span className="w-1/4">
                                        Permission Scope
                                    </span>
                                    <Select name="permission-scope">
                                        <SelectTrigger className="w-3/4">
                                            <SelectValue placeholder={"Select Permission Scope"}/>
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Permission Targets</SelectLabel>
                                                {Object.keys(PermissionScope).map((permissionScope, index) => (
                                                    <SelectItem value={permissionScope}
                                                                key={`permission-scope-select-${index}`}>{permissionScope}</SelectItem>))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </Label>
                            </div>

                            <div className="my-4">
                                <Label>
                                    <span className="w-1/4">
                                        Subject Type
                                    </span>
                                    <Select name="permission-target"
                                            onValueChange={value => setPermissionTarget(value as PermissionTargetType)}>
                                        <SelectTrigger className="w-3/4">
                                            <SelectValue placeholder={"Select Subject Type"}/>
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Permission Targets</SelectLabel>
                                                {PERMISSION_TYPES.map((type, index) => (<SelectItem value={type.value}
                                                                                                    key={`subject-select-${index}`}>{type.label}</SelectItem>))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </Label>
                            </div>


                            {permissionTarget === PermissionTargetType.USER && (
                                <div className="my-4">
                                    <Label>
                                    <span className="w-1/4">
                                        User
                                    </span>
                                        <Select name="user">
                                            <SelectTrigger className="w-3/4">
                                                <SelectValue placeholder={"Select User"}/>
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectGroup>
                                                    <SelectLabel>Users</SelectLabel>
                                                    {teams.flatMap(t => t.members).map((user, index) => (
                                                        <SelectItem value={user.id!}
                                                                    key={`resource-select-${index}`}>{user.username}</SelectItem>))}
                                                </SelectGroup>
                                            </SelectContent>
                                        </Select>
                                    </Label>
                                </div>
                            )}

                            {permissionTarget === PermissionTargetType.APP && (
                                <div className="my-4">
                                    <Label>
                                        <span className="w-1/4">Application</span>
                                        <Select name="application">
                                            <SelectTrigger className="w-3/4">
                                                <SelectValue placeholder={"Select Application"}/>
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectGroup>
                                                    <SelectLabel>Applications</SelectLabel>
                                                    {apps.map((app, index) => (
                                                        <SelectItem value={app.client_id} key={`apps-select-${index}`}>
                                                            {app.name}
                                                        </SelectItem>))}
                                                </SelectGroup>
                                            </SelectContent>
                                        </Select>
                                    </Label>
                                </div>
                            )}

                            {permissionTarget === PermissionTargetType.TEAM && (
                                <div className="my-4">
                                    <Label>
                                        <span className="w-1/4">
                                            Team
                                        </span>
                                        <Select name="team">
                                            <SelectTrigger className="w-3/4">
                                                <SelectValue placeholder={"Select Team"}/>
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectGroup>
                                                    <SelectLabel>Teams</SelectLabel>
                                                    {teams.map((team, index) => (
                                                        <SelectItem value={team.id!} key={`teams-select-${index}`}>
                                                            {team.name}
                                                        </SelectItem>
                                                    ))}
                                                </SelectGroup>
                                            </SelectContent>
                                        </Select>
                                    </Label>
                                </div>
                            )}

                        </div>

                    </div>
                    <SheetFooter>
                        <div className="flex flex-row gap-4">
                            <Button type={"submit"}>Save</Button>
                            <SheetClose asChild>
                                <Button variant={"outline"}>Close</Button>
                            </SheetClose>
                        </div>
                    </SheetFooter>
                </div>
            </form>
        </SheetContent>
    </Sheet>
}