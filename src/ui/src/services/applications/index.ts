import {Application} from "@models/Application.ts";
import {Authentication} from "@models/Auth.ts";
import {invokeGetRequest, invokePostRequest} from "@services/core";
import {ResponseWrapper} from "@models/ResponseWrapper.ts";

export async function getExternalApplicationInfo(client_id: string, redirectUri: string): Promise<Application | undefined> {
    const fetchUrl = `${window.location.origin}/api/apps/info?client_id=${client_id}&redirect_uri=${encodeURIComponent(redirectUri)}`;
    const fetchOptions: RequestInit = {
        headers: {
            "Authorization": `Basic `
        }
    }
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

export async function generateClientSecretKey(auth: Authentication, clientId: string): Promise<ResponseWrapper<string>> {
    return invokeGetRequest("/api/apps/generate-secret", {client_id: clientId}, auth.access_token);
}

export async function createNewApplication(auth: Authentication, app: Application) {
    return invokePostRequest("/api/apps/register",undefined, app, auth.access_token);
}

export function parseApplicationForm(formData: FormData): Application {
    return {
        client_id: "",
        uri: formData.get("application-url")!.toString(),
        name: formData.get("application-name")!.toString(),
        description: formData.get("application-description")!.toString(),
        scopes: [],
        redirect_uri: formData.get("callback-url")!.toString()
    }
}