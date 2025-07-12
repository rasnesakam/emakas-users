import {ExternalResourceInfo} from "@models/Resource.ts";

export async function getExternalResourceInfo(client_id: string, redirectUri?: string): Promise<ExternalResourceInfo | undefined> {
    return client_id ? Promise.resolve({redirectUri: redirectUri ?? "", resourceName: "Info", scopes: [], audiences: []}) : undefined;
}