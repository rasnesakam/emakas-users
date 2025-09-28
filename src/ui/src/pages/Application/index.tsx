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
import {Application} from "@models/Application.ts";
import {useAuthContext} from "@contexts/AuthContext";
import {getApplications} from "@services/applications";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@components/shadcn/ui/dropdown-menu.tsx";
import {EllipsisVertical} from "lucide-react";
import {Button} from "@components/shadcn/ui/button.tsx";

export function ApplicationPage() {
    const [applications, setApplications] = useState<Application[]>([]);
    const {auth} = useAuthContext();

    useEffect(() => {
        getApplications(auth!).then(setApplications);
    }, [auth]);

    return <>

        <div>
            <div className="flex flex-row justify-between">
                <div>
                    Registered Applications
                </div>
                <div className="flex flex-row justify-end">

                </div>
            </div>
            <Table>
                <TableCaption>Registered Applications</TableCaption>
                <TableHeader>
                    <TableRow>
                        <TableHead className="text-left">Application Name</TableHead>
                        <TableHead className="text-left">Application Uri</TableHead>
                        <TableHead className="text-left">Redirect Uri</TableHead>
                        <TableHead className="text-left">Application Description</TableHead>
                        <TableHead className="text-left"></TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {applications.map((item, index) => (<TableRow key={`permission-${index}`}>
                        <TableCell className="text-left">{item.name}</TableCell>
                        <TableCell className="text-left">{item.uri}</TableCell>
                        <TableCell className="text-left"><Button variant="link" asChild><a href={item.redirect_uri}>{item.redirect_uri}</a></Button></TableCell>
                        <TableCell className="text-left">{item.description}</TableCell>
                        <TableCell>
                            <DropdownMenu>
                                <DropdownMenuTrigger>
                                    <Button variant={"outline"}>
                                        <EllipsisVertical />
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent>
                                    <DropdownMenuLabel>Application Options</DropdownMenuLabel>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuGroup>
                                        <DropdownMenuItem>Copy Client Id</DropdownMenuItem>
                                        <DropdownMenuItem>Generate New Client Secret</DropdownMenuItem>
                                        <DropdownMenuItem variant={"destructive"}>Remove Application</DropdownMenuItem>
                                    </DropdownMenuGroup>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </TableCell>
                    </TableRow>))}
                </TableBody>
            </Table>
        </div>
    </>
}