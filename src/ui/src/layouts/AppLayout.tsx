import {Outlet} from "react-router";
import {Sidebar} from "@components/Sidebar";
import {NavBar} from "@components/NavBar";
import {JSX} from "react/jsx-runtime";

export function AppLayout({children}: {children?: JSX.Element}) {
    return <>
        <title>Users Dashboard</title>
        <meta name={"description"} content={"Manage your team here!"}/>
        <div>
            <NavBar />
            <div className="flex flex-row flex-start">
                <Sidebar className={"w-1/12"}/>
                <main className=" min-h-screen  w-11/12">
                    {children || <Outlet/>}
                </main>
            </div>
        </div>
        <footer></footer>
    </>
}