import {
    DialogClose,
    DialogContent,
    DialogDescription, DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@components/shadcn/ui/dialog.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {Input} from "@components/shadcn/ui/input.tsx";
import {Dialog} from "@radix-ui/react-dialog";
import {FormEvent, useState} from "react";
import {Resource} from "@models/Resource.ts";
import {createResourceFromFormData, saveNewResource} from "@services/resources";
import {useAuthContext} from "@contexts/AuthContext";
import {
    AlertDialog,
    AlertDialogContent,
    AlertDialogCancel, AlertDialogFooter, AlertDialogDescription, AlertDialogHeader, AlertDialogTitle
} from "@components/shadcn/ui/alert-dialog.tsx";

type AlertMessage = {
    title: string;
    message: string;
}

export function AddResourceFormDialog({afterSave}: {afterSave: (resource: Resource) => void}) {

    const {auth} = useAuthContext();
    const [isDialogOpen, setIsDialogOpen] = useState<boolean>(false);
    const [isAlertDialogOpen, setIsAlertDialogOpen] = useState<boolean>(false);
    const [alertMessage, setAlertMessage] = useState<AlertMessage>({title: "", message: ""})
    const submitNewResource = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const resource: Resource = createResourceFromFormData(formData)
        saveNewResource(resource, auth!)
            .then(resource => {
                setAlertMessage({
                    title: "Submission Successful",
                    message: "New Resource created successfully"
                });
                setIsAlertDialogOpen(true);
                setIsDialogOpen(false);
                return resource
            })
            .then(afterSave);
    }

    return <>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger>
                <Button>Add new Resource1</Button>
            </DialogTrigger>
            <DialogContent>
                <form onSubmit={submitNewResource}>
                    <DialogHeader>
                        <DialogTitle>Add New Resource</DialogTitle>
                        <DialogDescription>Resources are the services that users or applications
                            consume</DialogDescription>
                    </DialogHeader>
                    <div className="grid grid-cols-2 grid-rows-2 gap-2 mb-4">
                        <Input type="text" name="name" placeholder="Resource Name" className="col-span-1"/>
                        <Input type="text" name="uri" placeholder="Resource Uri" className="col-span-1"/>
                        <Input type="text" name="description" placeholder="Resource Description" className="col-span-2"/>
                    </div>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button>Cancel</Button>
                        </DialogClose>
                        <Button type="submit">Submit</Button>
                    </DialogFooter>
                </form>

            </DialogContent>
        </Dialog>
        <AlertDialog open={isAlertDialogOpen} onOpenChange={setIsAlertDialogOpen}>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>{alertMessage.title}</AlertDialogTitle>
                    <AlertDialogDescription>
                        {alertMessage.message}
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel>Close</AlertDialogCancel>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    </>
}