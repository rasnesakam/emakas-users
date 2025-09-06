import {ExternalResourceInfo, Resource} from "@models/Resource.ts";
import {invokeRestRequest, RestRequestType} from "@services/core";
import {ResponseWrapper} from "@models/ResponseWrapper.ts";
import {Authentication} from "@models/Auth.ts";

export async function getExternalResourceInfo(client_id: string, redirectUri?: string): Promise<ExternalResourceInfo | undefined> {
    return client_id ? Promise.resolve({redirectUri: redirectUri ?? "", resourceName: "Info", scopes: [], audiences: [], client_id}) : undefined;
}

export async function getAvailableResources(auth: Authentication): Promise<Resource[]> {

    const uri = "/api/resources/available"
    const extraOptions: RequestInit = {
        headers: {
            "Authorization": `Bearer ${auth.access_token}`
        }
    }
    return await invokeRestRequest<Resource[]>({uri, method: "GET", extraOptions})
}

export async function saveNewResource(resource: Resource, auth: Authentication): Promise<Resource> {
    const uri = "/api/resources/save";
    const extraOptions: RequestInit = {
        headers: {
            "Authorization": `Bearer ${auth.access_token}`,
            "Content-Type": "application/json"
        }
    };
    const restRequstParameters: RestRequestType = {
        uri, method: "POST", body: resource, extraOptions
    }
    return await invokeRestRequest<ResponseWrapper<Resource>>(restRequstParameters)
        .then(response => response.content);
}

export function createResourceFromFormData(formData: FormData): Resource {
    return {
        name: formData.get("name")!.toString(),
        uri: formData.get("uri")!.toString(),
        description: formData.get("description")!.toString()
    }
}