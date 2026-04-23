import {Outlet} from "react-router";
export function AuthLayout() {

    return <>
        <head></head>
        <main className="flex flex-col items-center justify-center min-h-screen">
            <Outlet/>
        </main>
        <footer></footer>
    </>
}