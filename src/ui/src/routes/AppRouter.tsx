import {Route, Routes} from "react-router";
import {AuthLayout} from "@layouts/AuthLayout.tsx";
import {LoginPage} from "@pages/Auth/Login";
import {HomePage} from "@pages/Home";
import {UnderConstruction} from "@pages/UnderConstruction";
import {NotFound} from "@pages/NotFound";
import {AccountPage} from "@pages/Account";
import {RestrictLayout} from "@layouts/RestrictLayout.tsx";

export function AppRouter (){

    return <>
        <Routes>
            <Route path={"page"}>
                <Route path={"auth"}>
                    <Route element={<AuthLayout/>}>
                        <Route path={"login"} element={<LoginPage/>}/>
                        <Route path={"logout"} element={<LoginPage/>}/>
                        <Route path={"sign-up"} element={<LoginPage/>}/>
                    </Route>
                </Route>
                <Route index element={<HomePage />}/>
                <Route element={<RestrictLayout/>}>
                    <Route path={"account"} element={<AccountPage />} />
                    <Route path={"teams"} element={<UnderConstruction />} />
                    <Route path={"resources"} element={<UnderConstruction />} />
                    <Route path={"permissions"} element={<UnderConstruction />} />
                </Route>
                <Route path={"*"} element={<NotFound />}/>
            </Route>
        </Routes>
    </>
}