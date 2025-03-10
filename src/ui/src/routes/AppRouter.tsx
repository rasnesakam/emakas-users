import {Route, Routes} from "react-router";
import {AuthLayout} from "@layouts/AuthLayout.tsx";
import {Login} from "@pages/auth/Login";
import {AppLayout} from "@layouts/AppLayout.tsx";

export function AppRouter (){

    return <>
        <Routes>
            <Route path={"auth"}>
                <Route element={<AuthLayout/>}>
                    <Route path={"login"} element={<Login/>}/>
                    <Route path={"logout"} element={<Login/>}/>
                    <Route path={"sign-up"} element={<Login/>}/>
                </Route>
            </Route>
            <Route path={"/"}>
                <Route element={<AppLayout/>}>
                    <Route path={"account"}>

                    </Route>
                    <Route path={"teams"}>

                    </Route>
                    <Route path={"resources"}>

                    </Route>
                    <Route path={"permissions"}>

                    </Route>
                </Route>
            </Route>
        </Routes>
    </>
}