import {LoginCredentials, LoginResponse} from "../../models/auth.ts";
import {getCookie} from "@utils/getToken.ts";

export async function login(credentials: LoginCredentials){
    const csrf = getCookie("XSRF-TOKEN");
    const audiences = "emakas.net";
    const scopes = "";
    if (!csrf)
        throw Error("CSRF token must be provided.")
    return fetch(`/api/auth/sign-in?audiences=${audiences}&scopes${scopes}`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrf
        },
        body: JSON.stringify(credentials)
    })
        .then(async response => {
            const loginResponse: LoginResponse = await response.json();
            if (response.ok)
                return loginResponse;
            throw new Error(loginResponse.message);
        })
}