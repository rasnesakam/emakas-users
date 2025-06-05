import {Outlet} from "react-router";
import {Sidebar} from "@components/Sidebar";
import {NavBar} from "@components/NavBar";

export function AppLayout() {
    return <>
        <title>Users Dashboard</title>
        <meta name={"description"} content={"Manage your team here!"}/>
        <div>
            <NavBar />
            <div className="flex flex-row flex-start">
                <Sidebar className={"w-1/12"}/>
                <main className=" min-h-screen  w-11/12">
                    <Outlet/>
                </main>
            </div>
        </div>
        <footer></footer>
    </>
}