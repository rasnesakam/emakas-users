import {Outlet} from "react-router";
export function AuthLayout() {

    return <>
        <head></head>
        <main>
            <Outlet/>
        </main>
        <footer></footer>
    </>
}