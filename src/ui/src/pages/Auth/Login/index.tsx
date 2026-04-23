import {/*FormEvent,*/ useEffect, useState} from "react";
import {useSearchParams} from "react-router";
// import {LoginCredentials} from "@models/Auth.ts";
// import {authorize, login} from "@services/auth";
import {LoadingComponent} from "@components/LoadingComponent";
import {getSelfApplicationOAuthRequest, initializeOAuthRequest} from "@utils/oauth";
import {OAuthRequest} from "@models/OAuth.ts";
import {getExternalApplicationInfo, getSelfApplicationInfo} from "@services/applications";
import {Application} from "@models/Application.ts";
import {Input} from "@components/shadcn/ui/input.tsx";
import {Button} from "@components/shadcn/ui/button.tsx";
import {InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput} from "@components/shadcn/ui/input-group.tsx";
import {Eye, EyeOff} from "lucide-react";
import {getCookie} from "@utils/utlis"

export function LoginPage(){
    const loginMessage: string = "Hesabınıza erişmek için oturum açın.";
    const [urlSearch] = useSearchParams();
    const [/*oAuthRequest*/, setOAuthRequest] = useState<OAuthRequest | undefined>(undefined);
    const [/*appInfo*/, setAppInfo] = useState<Application | undefined>(undefined);
    const [shouldPasswordVisible, setShouldPasswordVisible] = useState<boolean>(false);
    const [loadingState, /*setLoadingState*/] = useState<boolean>(false);

    useEffect(() => {
        try {
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
                getExternalApplicationInfo(oAuthRequest.client_id, oAuthRequest.redirect_uri)
                    .then(setAppInfo);
            }
            else {
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
        <div className="border rounded-md shadow-md px-10 pb-10 pt-2 bg-gray-50 w-2/3 xl:w-1/2 text-black">
            <LoadingComponent active={loadingState} />
            <div className="flex flex-row justify-start items-center pb-4">
                <img src="/vectors/icons8-e-100.svg" className="w-10 h-10" alt={"Logo E for Ensar"}/>
                <img src="/vectors/icons8-m-100.svg" className="w-10 h-10" alt={"Logo M for Makas"}/>
            </div>
            <div className="grid grid-cols-2">
                <div className="flex flex-col justify-start items-start">
                    <h1 className="text-3xl mb-4">Oturum Açın</h1>
                    <p>{loginMessage}</p>
                    {
                        urlSearch.has("error") && <p className="text-red-500 mt-2">{urlSearch.get("error") ?? "Unknown Error."}</p>
                    }
                </div>
                <div>
                    <form method="POST" action="/api/auth/sign-in" className="flex flex-col space-between">
                        <input type="hidden" name="_csrf" value={getCookie("XSRF-TOKEN")}/>
                        <input type="hidden" name="continue_uri" value={urlSearch.get("continue") ?? ""}/>
                        <div className="w-full my-2 border-b text-black">
                            <Input type={"text"} name={"username"} placeholder={"Kullanıcı Adı"}/>
                        </div>
                        <div className="w-full my-2 border-b text-black">
                            <InputGroup>
                                <InputGroupInput type={!shouldPasswordVisible ? "password" : "text"} name={"password"} placeholder={"Şifre"}/>
                                <InputGroupAddon align={"inline-end"}>
                                    <InputGroupButton onClick={() => setShouldPasswordVisible(!shouldPasswordVisible)}>
                                        {!shouldPasswordVisible ? <Eye/> : <EyeOff/>}
                                    </InputGroupButton>
                                </InputGroupAddon>
                            </InputGroup>
                        </div>
                        <div className="w-full flex flex-row justify-end gap-2">
                            <Button type={"button"} variant={"ghost"}>Şifremi Unuttum</Button>
                            <Button type={"submit"}>Giriş Yap</Button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

    </>
}