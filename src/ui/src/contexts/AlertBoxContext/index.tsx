import { createContext, useContext, useState, ReactNode } from "react";

import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@components/shadcn/ui/alert-dialog.tsx";

interface ConfirmOptions {
    title: string;
    description: string;
    confirmText?: string;
    cancelText?: string;
}

interface AlertBoxContextType {
    confirm: (options: ConfirmOptions) => Promise<void>;
}

const AlertBoxContext = createContext<AlertBoxContextType | undefined>(undefined);

export const AlertBoxProvider = ({ children }: { children: ReactNode }) => {
    const [open, setOpen] = useState(false);
    const [options, setOptions] = useState<ConfirmOptions | null>(null);
    // Promise'in resolve ve reject fonksiyonlarını saklamak için state
    const [callbacks, setCallbacks] = useState<{
        resolve: () => void;
        reject: () => void;
    } | null>(null);

    const confirm = (config: ConfirmOptions): Promise<void> => {
        setOptions(config);
        setOpen(true);

        return new Promise((resolve, reject) => {
            setCallbacks({ resolve, reject });
        });
    };

    const handleConfirm = () => {
        setOpen(false);
        callbacks?.resolve(); // .then() tetiklenir
    };

    const handleCancel = () => {
        setOpen(false);
        callbacks?.reject(); // .catch() tetiklenir
    };

    return (
        <AlertBoxContext.Provider value={{ confirm }}>
            {children}

            <AlertDialog open={open} onOpenChange={setOpen}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>{options?.title}</AlertDialogTitle>
                        <AlertDialogDescription>{options?.description}</AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel onClick={() => handleCancel()}>
                            {options?.cancelText || "Cancel"}
                        </AlertDialogCancel>
                        <AlertDialogAction onClick={() => handleConfirm()}>
                            {options?.confirmText || "Confirm"}
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </AlertBoxContext.Provider>
    );
};

export const useAlertBox = () => {
    const context = useContext(AlertBoxContext);
    if (!context) throw new Error("useConfirm, ConfirmProvider içinde kullanılmalıdır.");
    return context;
};