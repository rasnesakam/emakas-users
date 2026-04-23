import {useEffect, useState} from "react";
import {generateResourceSecretKey, getAllResources, getAvailableResources} from "@services/resources";
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
    DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem,
    DropdownMenuLabel, DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@components/shadcn/ui/dropdown-menu.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {Check, Copy, EllipsisVertical} from "lucide-react";
import {copyItemToClipboard} from "@utils/copyUtils";
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle
} from "@components/shadcn/ui/dialog.tsx";
import {InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput} from "@components/shadcn/ui/input-group.tsx";
import {useAlertBox} from "@contexts/AlertBoxContext";

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
    const [secretKey, setSecretKey] = useState("");
    const [secretKeyCopied, setSecretKeyCopied] = useState<boolean>(false);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const {auth} = useAuthContext();
    const alertBox = useAlertBox();

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

    function copyResourceId(resourceId: string) {
        copyItemToClipboard(resourceId, {
            title: "Client if coppied to clipboard",
            description: "You can use it for your application now"
        });
    }

    function copyResourceSecret(resourceSecret: string) {
        copyItemToClipboard(resourceSecret, {
            title: "Secret Key Copied!",
            description: "Please store it somewhere so you won't lose it!",
            dispatcher: setSecretKeyCopied
        })
    }

    function generateResourceSecret(clientId: string) {
        generateResourceSecretKey(auth!, clientId).then(response => {
            setSecretKey(response);
            setDialogOpen(true);
        })
    }

    function removeResource(resourceId: string) {
        alertBox.confirm({
            title: "Attention!",
            description: "This action cannot be undone. Are you sure to want to proceed?",
        }).then(() => {
            removeResource(resourceId)
        })
    }

    function toggleDialogOpenStateSage(dialogOpenState: boolean) {
        if (!dialogOpenState)
            setSecretKey("");
        setDialogOpen(dialogOpenState);
    }

    return <>
        <Dialog open={isDialogOpen} onOpenChange={toggleDialogOpenStateSage}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Client Secret Generated</DialogTitle>
                    <DialogDescription>
                        This is one time presentation of client secret. Please copy it and store it somewhere safe.<br/>
                        After close this dialog, you won't be able to see the secret again.
                    </DialogDescription>
                </DialogHeader>
                <div className="flex flex-row justify-center">
                    <InputGroup>
                        <InputGroupInput type="text" value={secretKey} readOnly={true}/>
                        <InputGroupAddon align="inline-end"/>
                        <InputGroupButton onClick={() => copyResourceSecret(secretKey)}>
                            {secretKeyCopied ? <Check/> : <Copy/>}
                        </InputGroupButton>
                    </InputGroup>
                </div>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant="outline">Close</Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
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
                        <TableHead className="text-left font-semibold">Resource Uri</TableHead>
                        <TableHead className="text-left font-semibold">Resource Description</TableHead>
                        <TableHead className="text-left font-semibold"></TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {resources.map((item, index) => (<TableRow key={`resource-${index}`}>
                        <TableCell className="text-left">{item.name}</TableCell>
                        <TableCell className="text-left">{item.uri}</TableCell>
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
                                        <DropdownMenuItem onClick={() => copyResourceId(item.id!)}>Copy Client Id</DropdownMenuItem>
                                        <DropdownMenuItem onClick={() => generateResourceSecret(item.id!)}>Generate New Client Secret</DropdownMenuItem>
                                        <DropdownMenuItem variant={"destructive"} onClick={() => removeResource(item.id!)}>Remove Resource</DropdownMenuItem>
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