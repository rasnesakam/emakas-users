import {FormEvent} from "react";

export function Login(){
    const onFormSubmit = (e: FormEvent) => {
        e.preventDefault();
    }
    return <>

        <form onSubmit={onFormSubmit}>
            <input type={"text"} name={"username"} placeholder={"Kullanıcı Adı"}/>
            <input type={"password"} name={"password"} placeholder={"Şifre"}/>
            <button>Giriş Yap</button>
        </form>
    </>
}