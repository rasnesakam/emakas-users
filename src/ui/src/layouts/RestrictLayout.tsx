import {Outlet} from "react-router";
import {AppLayout} from "@layouts/AppLayout.tsx";
import {useAuthContext} from "@contexts/AuthContext";
import {tryRefreshToken, validateToken} from "@services/auth";
import {useEffect, useRef, useState} from "react";
import {getSelfApplicationInfo} from "@services/applications";
import {getState} from "@utils/stateHolder";
import {getCodeChallenge} from "@utils/codeChallengeHolder";

export function RestrictLayout() {
    const {auth, setAuth} = useAuthContext();
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const refreshTimeoutRef = useRef<number | null>(null);


    const redirectToLoginPage = async () => {
        const appInfo = await getSelfApplicationInfo();
        const codeChallenge = await getCodeChallenge();
        const params = new URLSearchParams({
            response_type: "code",
            client_id: appInfo.client_id,
            redirect_uri: appInfo.redirect_uri,
            scope: "openid profile email",
            state: getState(),
            code_challenge: codeChallenge
        });
        const redirectUrl = `/oauth2/authorize?${params.toString()}`;
        return window.location.replace(redirectUrl);
    }

    useEffect(() => {
        const runAuthFlow = async () => {
            if (!auth) {
                await redirectToLoginPage();
                return;
            }
            const isValid = await validateToken(auth.access_token)
            if (isValid) {
                setIsAuthenticated(true);
            }
            else {
                const newCredentials = await tryRefreshToken(auth.refresh_token)
                if (newCredentials != undefined){
                    setAuth(newCredentials);
                    setIsAuthenticated(true);
                }
                else{
                    setAuth(undefined);
                    await redirectToLoginPage();
                }
            }
        }

        runAuthFlow()

        if (refreshTimeoutRef.current) {
            clearTimeout(refreshTimeoutRef.current);
        }

        if (!auth?.refresh_token || !auth?.expires_in) {
            return;
        }

        refreshTimeoutRef.current = window.setTimeout(async () => {
            const newCredentials = await tryRefreshToken(auth.refresh_token);
            if (newCredentials) {
                setAuth(newCredentials);
                setIsAuthenticated(true);
            } else {
                setAuth(undefined);
                redirectToLoginPage();
            }
        }, (auth.expires_in - 30) * 1000); // ⚠️ 30 sn önce yenile


        return () => {
            if (refreshTimeoutRef.current) {
                clearTimeout(refreshTimeoutRef.current);
            }
        };
    }, [auth]);
    if (!isAuthenticated)
        return null;
    return <>
        <AppLayout>
            <Outlet/>
        </AppLayout>
    </>
}