import {Application} from "@models/Application.ts";
import {Authentication} from "@models/Auth.ts";
import {invokeGetRequest} from "@services/core";

export async function getExternalApplicationInfo(client_id: string, redirectUri: string): Promise<Application | undefined> {
    const fetchUrl = `${window.location.origin}/api/apps/info?client_id=${client_id}&redirect_uri=${encodeURIComponent(redirectUri)}`;
    const fetchOptions: RequestInit = {}
    const fetchResponse = await fetch(fetchUrl, fetchOptions)

    if (fetchResponse.ok)
        return await fetchResponse.json() as unknown as Application
    throw new Error(fetchResponse.statusText)
}

export async function getApplications(auth: Authentication): Promise<Application[]> {
    const fetchUrl = `${window.location.origin}/api/apps/`;
    return invokeGetRequest<Application[]>(fetchUrl, undefined, auth.access_token);
}

export async function getSelfApplicationInfo(): Promise<Application>{
    const fetchUrl = `${window.location.origin}/api/apps/self`;
    const fetchOptions: RequestInit = {}
    const fetchResponse = await fetch(fetchUrl, fetchOptions)

    if (fetchResponse.ok)
        return await fetchResponse.json() as unknown as Application
    throw new Error(fetchResponse.statusText)
}