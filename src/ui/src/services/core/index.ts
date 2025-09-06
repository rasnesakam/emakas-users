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