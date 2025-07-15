import {OAuthRequestKeys} from "@utils/enums/OAuthEnums.ts";

export interface LoginCredentials {
    username: string;
    password: string;
    app_scopes?: string[];
    app_audiences?: string[]
    app_redirect?: string;
}

export interface LoginResponse {
    clientId: string;
    sessionId: string;
    redirectUri: string;
    audience: string;
    requestedScopes: string[];
    state: string;
}

export interface Authentication {
    name?: string;
    surname?: string;
    username: string;
    email: string;
    expires_in: number;
    refresh_token: string;
    access_token: string;
    token_type: string;
}

export interface OAuthRequest {
    response_type: "code";
    client_id: string;
    redirect_uri?: string;
    scope?: string;
    state?: string;
}

export function initializeOAuthRequest(searchParams: URLSearchParams): OAuthRequest {

    const client_id = searchParams.get(OAuthRequestKeys.CLIENT_ID);
    const redirect_uri = searchParams.get(OAuthRequestKeys.REDIRECT_URI);
    const response_type = searchParams.get(OAuthRequestKeys.RESPONSE_TYPE);
    const scope = searchParams.get(OAuthRequestKeys.SCOPE);
    const state = searchParams.get(OAuthRequestKeys.STATE);

    if (response_type !== "code")
        throw new Error(`Field ${OAuthRequestKeys.RESPONSE_TYPE} should be set as code`);
    if (!client_id)
        throw new Error(`Field ${OAuthRequestKeys.CLIENT_ID} is required`);
    return {
        client_id,
        redirect_uri: redirect_uri ?? undefined,
        response_type,
        scope: scope ?? undefined,
        state: state ?? undefined
    }
}

export enum TokenStatus {
    VALID,
    INVALID,
    EXPIRED
}

