import {Outlet} from "react-router";

import {JSX} from "react/jsx-runtime";
import {SidebarProvider } from "@components/shadcn/ui/sidebar.tsx";
import {AppSideBar} from "@components/AppSideBar";
import {Toaster} from "@components/shadcn/ui/sonner.tsx";


export function AppLayout({children}: {children?: JSX.Element}) {
    return <>
        <title>Users Dashboard</title>
        <meta name={"description"} content={"Manage your team here!"}/>
        <div>
            <SidebarProvider>
                <div className="flex flex-row flex-start w-full">
                    <AppSideBar/>
                    <Toaster />
                    <main className="p-10 w-11/12">
                        {children || <Outlet/>}
                    </main>
                </div>
            </SidebarProvider>
        </div>
        <footer></footer>
    </>
}