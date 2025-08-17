import {useNavigate, useSearchParams} from "react-router";
import {useEffect} from "react";
import {getState} from "@utils/stateHolder";
import {getToken, TokenOptions} from "@services/auth";
import {getCodeVerifier} from "@utils/codeChallengeHolder";
import {GrantType} from "@utils/enums/GrantType.ts";
import {getSelfApplicationInfo} from "@services/applications";
import {useAuthContext} from "@contexts/AuthContext";

const URL_SEARCH_CODE = "code";

const URL_SEARCH_STATE = "state";


export function CallbackPage() {
    const [urlSearch] = useSearchParams();
    const navigate = useNavigate();
    const {setAuth} = useAuthContext();

    useEffect(() => {
        async function executeOnLoad() {
            const grantCode = urlSearch.get(URL_SEARCH_CODE);
            const state = urlSearch.get(URL_SEARCH_STATE);
            if (grantCode) {
                const appInfo = await getSelfApplicationInfo();
                const appState = getState();
                if (appState === state) {
                    const codeVerifier = getCodeVerifier();
                    const tokenOptions: TokenOptions = {
                        code: grantCode,
                        clientId: appInfo.client_id,
                        redirectUri: appInfo.redirectUri,
                        codeVerifier: codeVerifier
                    }
                    getToken(GrantType.AUTHORIZATION_CODE, tokenOptions).then(auth => {
                        console.log(auth);
                        if (auth)
                        {
                            setAuth(auth);
                            alert("Navigating to home page");
                            navigate("/page/");
                        }
                    }).catch(err => console.error(err));
                }
            }
        }
        executeOnLoad();
    }, []);
    return <>
        <h1>Hang on, We logging you in</h1>
    </>
}