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
import {FormEvent} from "react";
import {Resource} from "@models/Resource.ts";
import {createResourceFromFormData, saveNewResource} from "@services/resources";
import {useAuthContext} from "@contexts/AuthContext";


export function AddResourceFormDialog({afterSave}: {afterSave: (resource: Resource) => void}) {

    const {auth} = useAuthContext();

    const submitNewResource = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const resource: Resource = createResourceFromFormData(formData)
        saveNewResource(resource, auth!)
            .then(resource => {
                // run other code rules
                return resource
            })
            .then(afterSave);
    }

    return <>
        <Dialog>
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
    </>
}