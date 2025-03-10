import {Route, Routes} from "react-router";
import {AuthLayout} from "@layouts/AuthLayout.tsx";
import {Login} from "@pages/Auth/Login";
import {AppLayout} from "@layouts/AppLayout.tsx";
import {Home} from "@pages/Home";
import {UnderConstruction} from "@pages/UnderConstruction";
import {NotFound} from "@pages/NotFound";

export function AppRouter (){

    return <>
        <Routes>
            <Route path={"page"}>
                <Route path={"auth"}>
                    <Route element={<AuthLayout/>}>
                        <Route path={"login"} element={<Login/>}/>
                        <Route path={"logout"} element={<Login/>}/>
                        <Route path={"sign-up"} element={<Login/>}/>
                    </Route>
                </Route>
                <Route index element={<Home />}/>
                <Route element={<AppLayout/>}>
                    <Route path={"account"} element={<UnderConstruction />}>

                    </Route>
                    <Route path={"teams"} element={<UnderConstruction />}>

                    </Route>
                    <Route path={"resources"} element={<UnderConstruction />}>

                    </Route>
                    <Route path={"permissions"} element={<UnderConstruction />} />
                </Route>
                <Route path={"*"} element={<NotFound />}/>
            </Route>
        </Routes>
    </>
}