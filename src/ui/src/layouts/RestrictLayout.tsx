import {Outlet, useNavigate} from "react-router";
import {AppLayout} from "@layouts/AppLayout.tsx";
import {useAuthContext} from "../contexts/AuthContext";
import {tryRefreshToken, validateToken} from "@services/auth";
import {useEffect, useState} from "react";

export function RestrictLayout() {
    const {auth, setAuth} = useAuthContext();
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const navigate = useNavigate();
    useEffect(() => {
        if (!auth) {
            navigate("/page/auth/login", {replace: true})
            return;
        }
        validateToken(auth.access_token).then(isValid => {
            if (isValid)
                setIsAuthenticated(true);
            else {
                tryRefreshToken(auth.refresh_token).then(newCredentials => {
                    if (newCredentials != undefined){
                        setAuth(newCredentials);
                        setIsAuthenticated(true);
                    }
                    else{
                        setAuth(undefined);
                        navigate("/page/auth/login", {replace: true})
                    }
                })
            }
        }).catch(err => console.error(err));
        const authIntervalHandler = () => {
            if (auth && auth.refresh_token){
                tryRefreshToken(auth.refresh_token).then((newCredentials) => {
                    if (newCredentials != undefined){
                        setAuth(newCredentials);
                        setIsAuthenticated(true);
                    }
                    else
                        navigate("/page/auth/login", {replace: true})
                });
            }
        }
        const authTimeOut = setTimeout(authIntervalHandler, auth.expires_in * 1000);
        return () => clearTimeout(authTimeOut);

    }, []);
    if (!isAuthenticated)
        return null;
    return <>
        <AppLayout>
            <Outlet/>
        </AppLayout>
    </>
}