export interface Application {
    client_id: string;
    redirect_uri: string;
    name: string;
    description: string;
    uri: string;
    scopes: string[];
}