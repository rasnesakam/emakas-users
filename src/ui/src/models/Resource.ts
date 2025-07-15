export interface ExternalResourceInfo {
    resourceName: string;
    client_id: string;
    redirectUri: string;
    audiences: string[];
    scopes: string[];
    state?: string;
}