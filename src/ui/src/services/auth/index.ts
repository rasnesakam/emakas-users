import {Authentication, LoginCredentials, LoginResponse} from "@models/Auth.ts";
import {getCookie} from "@utils/getToken.ts";
import {ResponseWrapper} from "@models/ResponseWrapper.ts";
import {OAuthGrantRequestKeys} from "@utils/enums/OAuthEnums.ts";

export async function login(credentials: LoginCredentials, client_id: string, state: string = ""): Promise<LoginResponse>{
    const csrf = getCookie("XSRF-TOKEN");
    const audiences = "emakas.net";
    const scopes = "";
    if (!csrf)
        throw Error("CSRF token must be provided.")
    return fetch(`/api/auth/sign-in?client_id=${client_id}&audiences=${audiences}&scopes${scopes}&state=${state}`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrf
        },
        body: JSON.stringify(credentials)
    })
        .then(async response => {
            const loginResponse: ResponseWrapper<LoginResponse> = await response.json();
            if (response.ok)
                return loginResponse.content;
            throw new Error(loginResponse.message);
        })
}

export async function authorize(loginResponse: LoginResponse) {
    const uri = `/api/auth/authorize?client_id=${loginResponse.clientId}&audiences=${loginResponse.audience}&scopes${loginResponse.requestedScopes}`;
    const formElement = document.createElement("form");
    const clientIdInput = document.createElement("input");
    const audienceInput = document.createElement("input");
    const scopeInput = document.createElement("input");
    const sessionIdInput = document.createElement("input");
    const stateInput = document.createElement("input");
    const redirectUriInput = document.createElement("input");

    sessionIdInput.type = "hidden";
    sessionIdInput.name = "session_id";
    sessionIdInput.value = loginResponse.sessionId
    formElement.appendChild(sessionIdInput);

    clientIdInput.type = "hidden";
    clientIdInput.name = "client_id";
    clientIdInput.value = loginResponse.clientId;
    formElement.appendChild(clientIdInput);

    audienceInput.type = "hidden";
    audienceInput.name = "audience";
    audienceInput.value = loginResponse.audience;
    formElement.appendChild(audienceInput);

    scopeInput.type = "hidden";
    scopeInput.name = "requested_scopes";
    scopeInput.value = loginResponse.requestedScopes.join(",");
    formElement.appendChild(scopeInput);

    stateInput.type = "hidden";
    stateInput.name = "state";
    stateInput.value = loginResponse.state;
    formElement.appendChild(stateInput);

    redirectUriInput.type = "hidden";
    redirectUriInput.name = "redirect_uri";
    redirectUriInput.value = loginResponse.redirectUri;
    formElement.appendChild(redirectUriInput);

    formElement.action = uri;
    formElement.method = "GET";
    document.body.appendChild(formElement);
    formElement.submit();
}

export async function getToken(grant: string): Promise<Authentication | undefined>{
    const formData = new FormData();

    formData.set(OAuthGrantRequestKeys.GRANT_TYPE, "token");
    formData.set(OAuthGrantRequestKeys.CODE, grant);

    return fetch(`/api/oauth/token/`).then(response =>{
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