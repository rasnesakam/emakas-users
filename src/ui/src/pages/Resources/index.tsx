import {useEffect, useState} from "react";
import {getAvailableResources} from "@services/resources";
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

export function ResourcesPage() {
    const [resources, setResources] = useState<Resource[]>([]);
    const {auth} = useAuthContext();

    useEffect(() => {
        if (auth)
            getAvailableResources(auth).then(setResources);
    }, []);



    return <>
        <div className="mx-auto w-full lg:w-11/12 xl:w-10/12 p-10">
            <div className="flex flex-row-reverse justify-start gap-4">
                <AddResourceFormDialog afterSave={(res) => setResources([...resources, res])} />
            </div>
            <Table>
                <TableCaption>Available Resources</TableCaption>
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