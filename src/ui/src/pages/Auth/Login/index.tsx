import {FormEvent, useEffect, useState} from "react";
import { useSearchParams} from "react-router";
import {ExternalResourceInfo} from "@models/Resource.ts";
import {getExternalResourceInfo} from "@services/resources";
import {initializeOAuthRequest, LoginCredentials, OAuthRequest} from "@models/Auth.ts";
import {authorize, login} from "@services/auth";
import {LoadingComponent} from "@components/LoadingComponent";
//import {useAuthContext} from "@contexts/AuthContext";



export function LoginPage(){
    //const { setAuth} = useAuthContext();
    const loginMessage: string = "Hesabınıza erişmek için oturum açın.";
    const [urlSearch] = useSearchParams();
    const [oAuthRequest, setOAuthRequest] = useState<OAuthRequest | undefined>(undefined);
    const [resourceInfo, setResourceInfo] = useState<ExternalResourceInfo | undefined>(undefined);
    //const navigate = useNavigate();
    const [loadingState, setLoadingState] = useState<boolean>(false);

    const onFormSubmit = (e: FormEvent) => {
        e.preventDefault();
        console.log("OAuth Request: ", oAuthRequest);
        console.log("External App Info: ", resourceInfo);
        setLoadingState(true);

        const formData = new FormData(e.target as HTMLFormElement);
        const loginInput: LoginCredentials = {
            username: formData.get("username")!.toString(),
            password: formData.get("password")!.toString(),
        }
        if (resourceInfo) {
            loginInput.app_redirect = resourceInfo.redirectUri;
            loginInput.app_audiences = resourceInfo.audiences;
            loginInput.app_scopes = resourceInfo.scopes;
        }
        login(loginInput, resourceInfo!.client_id, oAuthRequest?.state)
            .then(authorize)
            /*
            .then(grant => {
                if (oAuthRequest && resourceInfo){
                    const redirect = `${resourceInfo.redirectUri}?grant=${grant}`;
                    alert(`Redirecting to: ${redirect}`)
                    location.replace(redirect)
                }
                else{
                    getToken(grant).then(tokenCollection => {
                        console.log(tokenCollection)
                        if (tokenCollection){
                            setAuth(tokenCollection);
                            navigate("/page/", {replace: true});
                        }
                    }).catch(err => {
                        console.error(err);
                    });
                    // set token as cookie or session key

                }
            })
             */
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
            const oAuthRequest = initializeOAuthRequest(urlSearch);
            console.log("OAuth Request: ", oAuthRequest);
            setOAuthRequest(oAuthRequest);
            if (oAuthRequest){
                alert("External App")
                getExternalResourceInfo(oAuthRequest.client_id, oAuthRequest.redirect_uri)
                    .then(setResourceInfo);

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