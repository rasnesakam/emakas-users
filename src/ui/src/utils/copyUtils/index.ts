import {Dispatch, SetStateAction} from "react";
import {copyToClipboard} from "@utils/utlis.ts";
import {toast} from "sonner";

export function copyItemToClipboard(item: string, {title, description, dispatcher}:{title?: string, description?: string, dispatcher?: Dispatch<SetStateAction<boolean>>}) {
    copyToClipboard(item)
        .then(() => {
            if (dispatcher){
                dispatcher(true);
                setTimeout(() => dispatcher(false), 5000);
            }
        })
        .then(() => {
            toast(title, {
                description: description,
                action: {
                    label: "Ok",
                    onClick: () => {}
                }
            });
        })
}