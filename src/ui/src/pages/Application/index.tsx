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
import {generateClientSecretKey, getApplications} from "@services/applications";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@components/shadcn/ui/dropdown-menu.tsx";
import {Clipboard, EllipsisVertical} from "lucide-react";
import {Button} from "@components/shadcn/ui/button.tsx";
import {copyToClipboard} from "@utils/utlis.ts";
import {toast} from "sonner";
import {
    Dialog, DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle
} from "@components/shadcn/ui/dialog.tsx";
import {Input} from "@components/shadcn/ui/input.tsx";


export function ApplicationPage() {
    const [applications, setApplications] = useState<Application[]>([]);
    const {auth} = useAuthContext();
    const [isDialogOpen, setDialogOpen] = useState(false);
    const [secretKey, setSecretKey] = useState("");
    useEffect(() => {
        getApplications(auth!).then(setApplications);
    }, [auth]);

    function copyClientId(clientId: string) {
        copyToClipboard(clientId).then(() => {
            toast("Client if coppied to clipboard", {
                description: "You can use it for your application now",
                action: {
                    label: "Ok",
                    onClick: () => console.log("Toast closed")
                }
            });
            console.log("toasted")
        });
    }

    function generateClientSecret(clientId: string) {
        generateClientSecretKey(auth!, clientId).then(response => {
            if (response.content){
                setSecretKey(response.content);
                setDialogOpen(true);
            }
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
                    <Input type="text" value={secretKey} readOnly />
                    <Button variant="ghost" onClick={() => copyToClipboard(secretKey).then(() => toast("Secret Key Coppied!"))}>
                        <Clipboard />
                    </Button>
                </div>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant="outline">Close</Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
        <div>
            <div className="flex flex-row justify-between">
                <div className="text-xl font-semibold mb-4">
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
                                        <DropdownMenuItem onClick={() => copyClientId(item.client_id)}>Copy Client Id</DropdownMenuItem>
                                        <DropdownMenuItem onClick={() => generateClientSecret(item.client_id)}>Generate New Client Secret</DropdownMenuItem>
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