export interface Application {
    client_id: string;
    redirectUri: string;
    name: string;
    description: string;
    uri: string;
    scopes: string[];
}