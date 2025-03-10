import {Outlet} from "react-router";
import React from "react";
interface AppLayoutProps {
    children?: React.ReactNode;
}
export function AppLayout({children}: AppLayoutProps) {
    return <>
        <title>Users Dashboard</title>
        <meta name={"description"} content={"Manage your team here!"}/>
        <main>
            {children || <Outlet />}
        </main>
        <footer></footer>
    </>
}