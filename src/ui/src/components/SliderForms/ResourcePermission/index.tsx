import {
    Sheet, SheetClose,
    SheetContent,
    SheetDescription, SheetFooter,
    SheetHeader,
    SheetTitle,
    SheetTrigger
} from "@components/shadcn/ui/sheet.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {Input} from "@components/shadcn/ui/input.tsx";

export function ResourcePermissionSliderForm({title}: {title: string}) {

    return <Sheet>
        <SheetTrigger>
            <Button>{title}</Button>
        </SheetTrigger>
        <SheetContent>
            <form>
                <SheetHeader>
                    <SheetTitle>
                        Create New Resource Permission
                    </SheetTitle>
                    <SheetDescription>
                        Description
                    </SheetDescription>
                </SheetHeader>
                <Input placeholder={"Resource Name"} />
                <Input placeholder={"Resource Name"} />
                <Input placeholder={"Resource Name"} />
                <Input placeholder={"Resource Name"} />
                <SheetFooter>
                    <Button>Save</Button>
                    <SheetClose asChild>
                        <Button variant={"outline"}>Close</Button>
                    </SheetClose>
                </SheetFooter>
            </form>
        </SheetContent>
    </Sheet>
}