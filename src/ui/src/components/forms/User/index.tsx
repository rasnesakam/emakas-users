import {FormComponentParams} from "@models/FormComponentParams.ts";
import {User} from "@models/User.ts";
import {Input} from "@components/shadcn/ui/input.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {FormEvent, useState} from "react";
import {getUserFromFormData} from "@utils/formDataMappers";
import {getUserInvitationLink} from "@services/users";
import {Dialog, DialogContent, DialogHeader} from "@components/shadcn/ui/dialog.tsx";
import {DialogTitle} from "@radix-ui/react-dialog";


export function UserQuickForm({beforeSubmit ,afterSubmit}: FormComponentParams<User>) {
    const [formMessage, setFormMessage] = useState(<></>);
    const [isDialogOpen, setDialogOpen] = useState(false);
    const onFormSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const user = getUserFromFormData(formData)
        if (beforeSubmit)
            beforeSubmit(user);
        getUserInvitationLink(user).then(link => {
            setFormMessage(<>
                Here is the invite link. Please send it to the related person.
                <Input readOnly={true} value={link}/>
            </>)
            setDialogOpen(true);
            if (afterSubmit)
                afterSubmit(user);
        })
    }
    return <>
        <form onSubmit={onFormSubmit}>
            <div className="grid grid-cols-2 gap-2">
                <Input name="name" placeholder="Name"/>
                <Input name="surname" placeholder="Surname"/>
                <Input name="email" placeholder="Email" className="col-span-2" />
                <Input name="username" placeholder="Username" className="col-span-2" />
                <Button type="submit" className="col-span-2">Submit</Button>
            </div>
        </form>
        <Dialog open={isDialogOpen} onOpenChange={setDialogOpen}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>
                        User Form
                    </DialogTitle>
                </DialogHeader>
                <div className="p-5">
                    {formMessage}
                </div>
            </DialogContent>
        </Dialog>
    </>
}