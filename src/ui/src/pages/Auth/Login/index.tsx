import {FormEvent} from "react";
import {getCookie} from "@utils/getToken.ts";
import {openAlert} from "@utils/openAlert.ts";

export function Login(){
    const onFormSubmit = (e: FormEvent) => {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const csrf = getCookie("XSRF-TOKEN");
        const audiences = "emakas.net";
        const scopes = "";

        const loginInput = {
            username: formData.get("username")?.toString(),
            password: formData.get("password")?.toString(),
        }
        if (!csrf)
            openAlert("Güvenlik sebebiyle sayfa yenilenecektir.").then(() => location.reload());
        else {
            fetch(`/api/auth/sign-in?audiences=${audiences}&scopes${scopes}`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrf
                },
                body: JSON.stringify(loginInput)
            }).then(response => response.json()).then(json => console.log(json))
        }
    }
    return <>
        <div className="border rounded-md shadow-md px-10 pb-10 pt-2 bg-gray-50 w-2/3 text-black">
            <div className="h-1 bg-primary w-full -mt-2"></div>
            <div className="flex flex-row justify-start items-center pb-4">
                <img src="/vectors/icons8-e-100.svg" className="w-10 h-10"/>
                <img src="/vectors/icons8-m-100.svg" className="w-10 h-10"/>
            </div>
            <div className="grid grid-cols-2">
                <div className="flex flex-col justify-start items-start">
                    <h1 className="text-3xl mb-4">Oturum Açın</h1>
                    <p></p>
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