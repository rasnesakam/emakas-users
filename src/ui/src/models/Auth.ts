
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

export enum TokenStatus {
    VALID,
    INVALID,
    EXPIRED
}

