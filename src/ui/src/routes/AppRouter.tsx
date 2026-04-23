import {Navigate, Route, Routes} from "react-router";
import {AuthLayout} from "@layouts/AuthLayout.tsx";
import {LoginPage} from "@pages/Auth/Login";
import {HomePage} from "@pages/Home";
import {UnderConstruction} from "@pages/UnderConstruction";
import {NotFound} from "@pages/NotFound";
import {AccountPage} from "@pages/Account";
import {RestrictLayout} from "@layouts/RestrictLayout.tsx";
import {TeamsPage} from "@pages/Teams";
import {CallbackPage} from "@pages/Callback";
import {ResourcesPage} from "@pages/Resources";
import {PermissionsPage} from "@pages/Permissions";
import {ApplicationPage} from "@pages/Application";
import {ErrorPage} from "@pages/Error";

export function AppRouter (){

    return <>
        <Routes>
            <Route path={"page"}>
                <Route path={"auth"}>
                    <Route element={<AuthLayout/>}>
                        <Route path={"login"} element={<LoginPage/>}/>
                        <Route path={"logout"} element={<UnderConstruction/>}/>
                        <Route path={"sign-up"} element={<UnderConstruction/>}/>
                    </Route>
                </Route>
                <Route path={"error"} element={<ErrorPage/>} />
                <Route element={<RestrictLayout/>}>
                    <Route index element={<Navigate to="home" replace />} />
                    <Route path={"home"} element={<HomePage />}/>
                    <Route path={"account"} element={<AccountPage />} />
                    <Route path={"teams"} element={<TeamsPage />} />
                    <Route path={"resources"} element={<ResourcesPage />} />
                    <Route path={"permissions"} element={<PermissionsPage />} />
                    <Route path={"applications"} element={<ApplicationPage />} />
                </Route>
                <Route path={"callback"} element={<CallbackPage />}/>
                <Route path={"*"} element={<NotFound />}/>
            </Route>
        </Routes>
    </>
}