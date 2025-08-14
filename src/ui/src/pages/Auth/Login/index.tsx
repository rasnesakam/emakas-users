import {FormEvent, useEffect, useState} from "react";
import {useSearchParams} from "react-router";
import {LoginCredentials} from "@models/Auth.ts";
import {authorize, login} from "@services/auth";
import {LoadingComponent} from "@components/LoadingComponent";
import {getSelfApplicationOAuthRequest, initializeOAuthRequest} from "@utils/oauth";
import {OAuthRequest} from "@models/OAuth.ts";
import {getExternalApplicationInfo, getSelfApplicationInfo} from "@services/applications";
import {Application} from "@models/Application.ts";
//import {useAuthContext} from "@contexts/AuthContext";

export function LoginPage(){
    //const { setAuth} = useAuthContext();
    const loginMessage: string = "Hesabınıza erişmek için oturum açın.";
    const [urlSearch] = useSearchParams();
    const [oAuthRequest, setOAuthRequest] = useState<OAuthRequest | undefined>(undefined);
    const [appInfo, setAppInfo] = useState<Application | undefined>(undefined);
    //const navigate = useNavigate();
    const [loadingState, setLoadingState] = useState<boolean>(false);

    const onFormSubmit = (e: FormEvent) => {
        e.preventDefault();
        console.log("OAuth.ts Request: ", oAuthRequest);
        console.log("External App Info: ", appInfo);
        setLoadingState(true);

        const formData = new FormData(e.target as HTMLFormElement);
        const loginInput: LoginCredentials = {
            username: formData.get("username")!.toString(),
            password: formData.get("password")!.toString(),
        }
        if (appInfo) {
            loginInput.app_redirect = appInfo.redirectUri;
            loginInput.app_audiences = [appInfo.uri];
            loginInput.app_scopes = appInfo.scopes;
        }
        login(loginInput, appInfo!.client_id, oAuthRequest?.state)
            .then(authorize)
            .catch(err => {
                console.error(err)
            })
            .finally(() => {

                setLoadingState(false);
            })


    }


    useEffect(() => {
        try {
            alert("check for external app")
            let oAuthRequest;
            try {
                oAuthRequest = initializeOAuthRequest(urlSearch);
                setOAuthRequest(oAuthRequest);
            }
            catch (e) {
                console.error(e);
                getSelfApplicationOAuthRequest().then(setOAuthRequest);
            }
            if (oAuthRequest){
                alert("External App")
                getExternalApplicationInfo(oAuthRequest.client_id, oAuthRequest.redirect_uri)
                    .then(setAppInfo);
            }
            else {
                alert("Internal App");
                getSelfApplicationInfo().then(app => {
                    console.log(app);
                    setAppInfo(app);
                });
            }
        } catch (err) {
            console.error(err);
        }
    }, []);

    return <>
        <div className="border rounded-md shadow-md px-10 pb-10 pt-2 bg-gray-50 w-2/3 text-black">
            <LoadingComponent active={loadingState} />
            <div className="flex flex-row justify-start items-center pb-4">
                <img src="/vectors/icons8-e-100.svg" className="w-10 h-10" alt={"Logo E for Ensar"}/>
                <img src="/vectors/icons8-m-100.svg" className="w-10 h-10" alt={"Logo M for Makas"}/>
            </div>
            <div className="grid grid-cols-2">
                <div className="flex flex-col justify-start items-start">
                    <h1 className="text-3xl mb-4">Oturum Açın</h1>
                    <p>{loginMessage}</p>
                </div>
                <div>
                    <form onSubmit={onFormSubmit} className="flex flex-col space-between">
                        <div className="w-full my-2 border-b text-black">
                            <input type={"text"} name={"username"} placeholder={"Kullanıcı Adı"}
                                   className="w-full focus-visible:outline-none"/>
                        </div>
                        <div className="w-full my-2 border-b text-black">
                            <input type={"password"} name={"password"} placeholder={"Şifre"}
                                   className="w-full focus-visible:outline-none"/>
                        </div>
                        <div className="w-full flex flex-row justify-end gap-2">
                            <button type={"button"} className="p-2  hover:bg-secondary hover:rounded-lg  hover:text-white">Şifremi Unuttum</button>
                            <button type={"submit"} className="bg-primary p-2 rounded-lg  text-white">Giriş Yap</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    </>
}