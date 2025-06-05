export interface LoginCredentials {
    username: string;
    password: string;
    app_scopes?: string[];
    app_audiences?: string[]
    app_redirect?: string;
}

export interface LoginResponse {
    content: string;
    message: string;
}

export interface TokenCollection {
    accessToken: string,
    refreshToken: string
}