import {FormEvent, useState} from "react";
import {useNavigate, useSearchParams} from "react-router";
import {ExternalResourceInfo} from "../../../models/resources.ts";
import {getExternalResourceInfo} from "@services/resources";
import {LoginCredentials} from "../../../models/auth.ts";
import {getToken, login} from "@services/auth";
import {LoadingComponent} from "@components/LoadingComponent";
import {LoginMethods} from "@utils/enums/LoginMethods.ts";
import {useAuthContext} from "../../../contexts/AuthContext";

enum PageParameters {
    PUBLIC_KEY = "public_key",
    REDIRECT_URL = "redirect"
}

export function LoginPage(){
    const { setAuth} = useAuthContext();
    const loginMessage: string = "Hesabınıza erişmek için oturum açın.";
    const [urlSearch] = useSearchParams();
    const [resourceInfo, setResourceInfo] = useState<ExternalResourceInfo | undefined>(undefined);
    const [loginMethod, setLoginMethod] = useState(LoginMethods.INTERNAL);
    const navigate = useNavigate();
    const [loadingState, setLoadingState] = useState<boolean>(false);
    if (urlSearch.has(PageParameters.REDIRECT_URL) && urlSearch.has(PageParameters.PUBLIC_KEY) ) {
        const publicKey: string = urlSearch.get(PageParameters.PUBLIC_KEY)!;
        const redirectUri: string = urlSearch.get(PageParameters.REDIRECT_URL)!;
        getExternalResourceInfo(publicKey, redirectUri)
            .then(resource => setResourceInfo(resource))
            .then(() => setLoginMethod(LoginMethods.EXTERNAL))
    }

    const onFormSubmit = (e: FormEvent) => {
        e.preventDefault();
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
        login(loginInput)
            .then(grant => {
                if (loginMethod === LoginMethods.EXTERNAL && resourceInfo != undefined){
                    location.replace(`${resourceInfo.redirectUri}?grant=${grant}`)
                }
                else if (loginMethod === LoginMethods.INTERNAL){
                    getToken(grant).then(tokenCollection => {
                        console.log(tokenCollection)
                        if (tokenCollection){
                            setAuth(tokenCollection);
                            navigate("/page/account", {replace: true});
                        }
                    }).catch(err => {
                        console.error(err);
                    });
                    // set token as cookie or session key

                }
            })
            .catch(err => {
                console.error(err)
            })
            .finally(() => {

                setLoadingState(false);
            })


    }


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