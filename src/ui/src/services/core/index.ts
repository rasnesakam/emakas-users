import {encodeBase64Url} from "@utils/crypto";

export interface RestRequestType {
    uri: string;
    method: "GET" | "POST" | "PUT" | "DELETE";
    body?: unknown;
    queryParams?: unknown;
    extraOptions?: RequestInit;
}

export async function invokeRestRequest<O>({uri, method, body, queryParams, extraOptions}: RestRequestType): Promise<O> {

    const fetchOptions: RequestInit = {
        method,
        ...extraOptions
    }
    if (body)
        fetchOptions.body = JSON.stringify(body);
    if (queryParams){
        uri = uri.concat('?').concat(Object.entries(queryParams).map(([k, v]) => `${k}=${encodeBase64Url(v.toString())}`).join('&'));
    }
    console.log("fetching with options: ", fetchOptions)
    const fetchResult = await fetch(uri, fetchOptions);
    if (!fetchResult.ok)
        throw new Error(`${fetchResult.status} - ${fetchResult.statusText}`)
    return await fetchResult.json() as unknown as O;
}

async function invokeRequest<O>(uri: string, method: "GET" | "POST" | "PUT" | "DELETE", queryParams: unknown | undefined = undefined, bodyParameters: unknown | undefined = undefined, jwtToken: string | undefined = undefined) {
    const invokeOptions: RestRequestType = {
        uri,
        method,
        body: bodyParameters,
        queryParams,
        extraOptions: {}
    }
    if (jwtToken)
        invokeOptions.extraOptions = {
            ...invokeOptions.extraOptions,
            headers: {
                ...invokeOptions.extraOptions?.headers,
                "Authorization": `Bearer ${jwtToken}`,
                "Content-Type": "application/json"
            }
        }
    return invokeRestRequest<O>(invokeOptions)
}

export async function invokeGetRequest<O>(uri: string, queryParams: unknown | undefined = undefined, jwtToken: string | undefined = undefined): Promise<O> {
    return invokeRequest<O>(uri, "GET", queryParams, undefined, jwtToken);
}

export async function invokePostRequest<O>(uri: string, queryParams: unknown | undefined = undefined, bodyParams: unknown | undefined = undefined, jwtToken: string | undefined = undefined): Promise<O> {
    return invokeRequest<O>(uri, "POST", queryParams, bodyParams, jwtToken);
}