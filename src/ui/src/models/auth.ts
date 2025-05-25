export interface LoginCredentials {
    username: string;
    password: string;
}

export interface LoginResponse {
    content: string;
    message: string;
}

export interface TokenCollection {
    accessToken: string,
    refreshToken: string
}