import {Authentication, LoginCredentials } from "../../models/Auth.ts";
import {getCookie} from "@utils/getToken.ts";
import {ResponseWrapper} from "../../models/ResponseWrapper.ts";

export async function login(credentials: LoginCredentials): Promise<string>{
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
            const loginResponse: ResponseWrapper<string> = await response.json();
            if (response.ok)
                return loginResponse.content;
            throw new Error(loginResponse.message);
        })
}

export async function getToken(grant: string): Promise<Authentication | undefined>{
    return fetch(`/api/oauth/token/issue?grant=${grant}`).then(response =>{
        if (response.ok)
            return response.json() as Promise<ResponseWrapper<Authentication>>;
        throw new Error(response.statusText)
    }).then(data => data.content as Authentication)
        .catch(err => {
            console.error(err);
            return undefined;
        })
}

export async function validateToken(token: string): Promise<boolean> {
    const fetchOptions: RequestInit = {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    }
    return fetch(`/api/oauth/token/verify`, fetchOptions).then(response => response.ok)
}

export async function tryRefreshToken(refreshToken: string): Promise<Authentication | undefined> {
    const fetchOptions: RequestInit = {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${refreshToken}`
        }
    }
    return fetch(`/api/oauth/token/refresh`, fetchOptions).then(response => {
        if (response.ok)
            return response.json()
        throw new Error(response.statusText)
    }).then(data => data as Authentication)
        .catch(err => {
            console.error(err);
            return undefined;
        })
}

const AUTH_KEY = "auth";
export function retrieveLocalAuthentication(): Authentication | undefined {
    const authString = window.localStorage.getItem(AUTH_KEY);
    if (!authString)
        return undefined;
    return JSON.parse(authString) as Authentication;
}
export function registerLocalAuthentication(auth: Authentication | undefined): Authentication | undefined {
    if (!auth)
        window.localStorage.removeItem(AUTH_KEY);
    else
        window.localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
    return auth;
}