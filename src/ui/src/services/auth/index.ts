import {Authentication, LoginCredentials, LoginResponse} from "@models/Auth.ts";
import {getCookie} from "@utils/getToken.ts";
import {ResponseWrapper} from "@models/ResponseWrapper.ts";
import {OAuthGrantRequestKeys} from "@utils/enums/OAuthEnums.ts";
import {GrantType} from "@utils/enums/GrantType.ts";
import {CodeChallengeMethod} from "@utils/enums/CodeChallengeMethod.ts";

export async function login(credentials: LoginCredentials, client_id: string, state: string = "", code_challenge: string = ""): Promise<LoginResponse>{
    const csrf = getCookie("XSRF-TOKEN");
    const audiences = credentials.app_audiences?.map(aud => `audiences=${aud}`).join("&") ?? "audiences=";
    const scopes = credentials.app_scopes?.map(scp => `scopes=${scp}`).join("&") ?? "scopes=";
    if (!csrf)
        throw Error("CSRF token must be provided.")
    return fetch(`/api/auth/sign-in?client_id=${client_id}&${audiences}&${scopes}&state=${state}&code_challenge=${code_challenge}`, {
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
    const uri = `/api/auth/authorize`;
    const formElement = document.createElement("form");
    const clientIdInput = document.createElement("input");
    const audienceInput = document.createElement("input");
    const scopeInput = document.createElement("input");
    const sessionIdInput = document.createElement("input");
    const stateInput = document.createElement("input");
    const codeChallengeInput = document.createElement("input");
    const codeChallengeMethodInput = document.createElement("input");
    const redirectUriInput = document.createElement("input");

    sessionIdInput.type = "hidden";
    sessionIdInput.name = "session_id";
    sessionIdInput.value = loginResponse.session_id
    formElement.appendChild(sessionIdInput);

    clientIdInput.type = "hidden";
    clientIdInput.name = "client_id";
    clientIdInput.value = loginResponse.client_id;
    formElement.appendChild(clientIdInput);

    audienceInput.type = "hidden";
    audienceInput.name = "audience";
    audienceInput.value = loginResponse.audience;
    formElement.appendChild(audienceInput);

    if (loginResponse.requested_scopes && loginResponse.requested_scopes.length > 0){
        for (const scope of loginResponse.requested_scopes) {
            const scopeInput = document.createElement("input");
            scopeInput.type = "hidden";
            scopeInput.name = "granted_scopes";
            scopeInput.value = scope;
            formElement.appendChild(scopeInput);
        }
    }

    if (loginResponse.state){
        stateInput.type = "hidden";
        stateInput.name = "state";
        stateInput.value = loginResponse.state;
        formElement.appendChild(stateInput);
    }

    if (loginResponse.code_challenge){
        codeChallengeInput.type = "hidden";
        codeChallengeInput.name = "code_challenge";
        codeChallengeInput.value = loginResponse.code_challenge;
        formElement.appendChild(codeChallengeInput);

        codeChallengeMethodInput.type = "hidden";
        codeChallengeMethodInput.name = "code_challenge_method";
        codeChallengeMethodInput.value = CodeChallengeMethod.SHA_256;
        formElement.appendChild(codeChallengeMethodInput);
    }

    redirectUriInput.type = "hidden";
    redirectUriInput.name = "redirect_uri";
    redirectUriInput.value = loginResponse.redirect_uri;
    formElement.appendChild(redirectUriInput);

    formElement.action = uri;
    formElement.method = "GET";
    document.body.appendChild(formElement);
    formElement.submit();
}

export interface TokenOptions {
    clientId?: string;
    redirectUri?: string;
    codeVerifier?: string;
    refreshToken?: string;
    code?: string;
}

export async function getToken(grantType: GrantType, {clientId, redirectUri, codeVerifier, refreshToken, code} : TokenOptions): Promise<Authentication | undefined>{
    const params = new URLSearchParams();

    params.set(OAuthGrantRequestKeys.GRANT_TYPE, grantType);
    if (code)
        params.set(OAuthGrantRequestKeys.CODE, code);
    if (clientId)
        params.set(OAuthGrantRequestKeys.CLIENT_ID, clientId);
    if (redirectUri)
        params.set(OAuthGrantRequestKeys.REDIRECT_URI, redirectUri);
    if (codeVerifier)
        params.set(OAuthGrantRequestKeys.CODE_VERIFIER, codeVerifier);
    if (refreshToken)
        params.set(OAuthGrantRequestKeys.REFRESH_TOKEN, refreshToken);

    const fetchOptions: RequestInit = {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params.toString()
    }

    return fetch(`/api/oauth/token`, fetchOptions).then(response =>{
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