import {
    Dialog, DialogClose,
    DialogContent,
    DialogDescription, DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@components/shadcn/ui/dialog.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {Input} from "@components/shadcn/ui/input.tsx";
import {FormEvent, useState} from "react";
import {JSX} from "react/jsx-runtime";
import {getUserInvitationLink} from "@services/users";
import {getUserFromFormData} from "@utils/formDataMappers";

interface FormMessage {
    title: string
    description: JSX.Element
}

export function InviteNewUserFormDialog() {

    const [messageDialogOpen, setMessageDialogOpen] = useState<boolean>(false);
    const [formMessage, setFormMessage] = useState<FormMessage>({title: "", description: <></>})

    const onFormSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const user = getUserFromFormData(formData);
        getUserInvitationLink(user).then(link => {
            setFormMessage({
                title: "User Invited!",
                description: <>
                    Here is the invite link. Please send it to the related person.
                    <Input readOnly={true} value={link}/>
                </>
            })
        }).then(() => setMessageDialogOpen(true))
    }

    return <>
        <Dialog>
            <DialogTrigger>
                <Button>Invite New Member</Button>
            </DialogTrigger>
            <DialogContent>
                <form onSubmit={onFormSubmit}>
                    <DialogHeader>
                        <DialogTitle>Invite User</DialogTitle>
                        <DialogDescription>Invite new user for the team</DialogDescription>
                    </DialogHeader>
                    <div className="grid grid-cols-2 gap-2 mb-4">
                        <Input name="name" placeholder="Name" required />
                        <Input name="surname" placeholder="Surname" required/>
                        <Input name="email" placeholder="Email" className="col-span-2" required/>
                        <Input name="username" placeholder="Username" className="col-span-2" required/>
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
        <Dialog open={messageDialogOpen} onOpenChange={setMessageDialogOpen}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>{formMessage.title}</DialogTitle>
                    <DialogDescription>{formMessage.description}</DialogDescription>
                </DialogHeader>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button>Close</Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    </>
}