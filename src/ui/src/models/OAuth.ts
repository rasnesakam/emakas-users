export interface OAuthRequest {
    response_type: "code";
    client_id: string;
    redirect_uri: string;
    scope?: string[];
    state?: string;
}