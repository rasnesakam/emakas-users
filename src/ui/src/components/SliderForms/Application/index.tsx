import {
    Sheet,
    SheetClose,
    SheetContent, SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle,
    SheetTrigger
} from "@components/shadcn/ui/sheet.tsx";
import {FormEvent, useState} from "react";
import {Button} from "@components/shadcn/ui/button.tsx";
import {Label} from "@components/shadcn/ui/label.tsx";
import {Input} from "@components/shadcn/ui/input.tsx";
import {useAuthContext} from "@contexts/AuthContext";
import {createNewApplication, parseApplicationForm} from "@services/applications";

export function ApplicationSliderForm({title}: {title: string}) {
    const [isSheetOpen, setIsSheetOpen] = useState<boolean>(false);
    const {auth} = useAuthContext();

    const formHandler = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const app = parseApplicationForm(new FormData(e.target as HTMLFormElement))
        createNewApplication(auth!, app).then(() => {
            alert("Eklendi sanırım");
            setIsSheetOpen(!isSheetOpen);

        });
    };

    return <>
        <Sheet open={isSheetOpen} onOpenChange={setIsSheetOpen}>
            <SheetTrigger>
                <Button>{title}</Button>
            </SheetTrigger>
            <SheetContent>
                <form onSubmit={formHandler} className="h-full">
                    <div className="flex flex-col justify-between h-full">
                        <div className="h-full">
                            <SheetHeader>
                                <SheetTitle>
                                    Create New Application
                                </SheetTitle>
                                <SheetDescription>
                                    Applications are thrid party softwares that uses your resources
                                </SheetDescription>
                            </SheetHeader>
                            <div className="p-4">
                                <div className="my-4">
                                    <Label>
                                        <span className="w1/4">Application Name</span>
                                        <Input name="application-name"/>
                                    </Label>
                                </div>
                                <div className="my-4">
                                    <Label>
                                        <span className="w1/4">Application Description</span>
                                        <Input name="application-description"/>
                                    </Label>
                                </div>
                                <div className="my-4">
                                    <Label>
                                        <span className="w1/4">Application URL</span>
                                        <Input name="application-url"/>
                                    </Label>
                                </div>
                                <div className="my-4">
                                    <Label>
                                        <span className="w1/4">Callback URL</span>
                                        <Input name="callback-url"/>
                                    </Label>
                                </div>
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
    </>
}