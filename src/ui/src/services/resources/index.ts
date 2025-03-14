import {ExternalResourceInfo} from "../../models/resources.ts";

export async function getExternalResourceInfo(publicKey: string, redirectUri: string): Promise<ExternalResourceInfo | undefined> {
    return !publicKey ? Promise.resolve({redirectUri, resourceName: "Info", scopes: [], audiences: []}) : undefined;
}