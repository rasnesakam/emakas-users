import {useEffect, useState} from "react";
import {getAllResources, getAvailableResources} from "@services/resources";
import {Resource} from "@models/Resource.ts";
import {
    Table,
    TableBody,
    TableCaption,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from "@components/shadcn/ui/table.tsx";
import {useAuthContext} from "@contexts/AuthContext";

import {AddResourceFormDialog} from "@components/formDialogs/AddResourceFormDialog";
import {
    DropdownMenu,
    DropdownMenuContent, DropdownMenuItem,
    DropdownMenuLabel, DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@components/shadcn/ui/dropdown-menu.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";

type FetchType = {
    name: string,
    value: "all" | "available",
};
const fetchingTypes: FetchType[] = [
    {name: "All Resources", value: "all"},
    {name: "Available Resources", value: "available"}
]

export function ResourcesPage() {
    const [resources, setResources] = useState<Resource[]>([]);
    const [fetchType, setFetchType] = useState<FetchType>(fetchingTypes[1]);
    const {auth} = useAuthContext();

    useEffect(() => {
        if (auth){
            let fetchPromise: Promise<Resource[]>;
            if (fetchType.value === "all")
                fetchPromise = getAllResources(auth);
            else // if (fetchType === "available")
                fetchPromise = getAvailableResources(auth);
            fetchPromise.then(setResources);
        }
    }, [fetchType]);



    return <>
        <div className="mx-auto w-full lg:w-11/12 xl:w-10/12 p-10">
            <div className="flex flex-row justify-between gap-4">
                <div className="flex flex-row justify-start gap-4">
                    <DropdownMenu>
                        <DropdownMenuTrigger><Button variant={"outline"}>{fetchType.name}</Button></DropdownMenuTrigger>
                        <DropdownMenuContent>
                            <DropdownMenuLabel>Resource Views</DropdownMenuLabel>
                            <DropdownMenuSeparator />
                            {fetchingTypes.map((item, index) => (<DropdownMenuItem onClick={() => setFetchType(fetchingTypes[index])} key={index}>
                                {item.name}
                            </DropdownMenuItem>))}
                        </DropdownMenuContent>
                    </DropdownMenu>
                </div>
                <div className="flex flex-row-reverse justify-start gap-4">
                    <AddResourceFormDialog afterSave={(res) => setResources([...resources, res])} />
                </div>
            </div>
            <Table>
                <TableCaption>{fetchType.name}</TableCaption>
                <TableHeader>
                    <TableRow>
                        <TableHead className="text-left font-semibold">Resource Name</TableHead>
                        <TableHead className="text-right font-semibold">Resource Description</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {resources.map((item, index) => (<TableRow key={`resource-${index}`}>
                        <TableCell className="text-left">{item.name}</TableCell>
                        <TableCell className="text-right">{item.description}</TableCell>
                    </TableRow>))}
                </TableBody>
            </Table>
        </div>
    </>
}