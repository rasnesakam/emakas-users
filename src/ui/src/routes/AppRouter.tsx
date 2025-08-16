import {Route, Routes} from "react-router";
import {AuthLayout} from "@layouts/AuthLayout.tsx";
import {LoginPage} from "@pages/Auth/Login";
import {HomePage} from "@pages/Home";
import {UnderConstruction} from "@pages/UnderConstruction";
import {NotFound} from "@pages/NotFound";
import {AccountPage} from "@pages/Account";
import {RestrictLayout} from "@layouts/RestrictLayout.tsx";
import {TeamsPage} from "@pages/Teams";
import {CallbackPage} from "@pages/Callback";

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
                <Route element={<RestrictLayout/>}>
                    <Route index element={<HomePage />}/>
                    <Route path={"account"} element={<AccountPage />} />
                    <Route path={"teams"} element={<TeamsPage />} />
                    <Route path={"resources"} element={<UnderConstruction />} />
                    <Route path={"permissions"} element={<UnderConstruction />} />
                    <Route path={"applications"} element={<UnderConstruction />} />
                </Route>
                <Route path={"callback"} element={<CallbackPage />}/>
                <Route path={"*"} element={<NotFound />}/>
            </Route>
        </Routes>
    </>
}