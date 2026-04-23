import {Authentication} from "@models/Auth.ts";
import {Team} from "@models/Team.ts";

export async function getUserTeams(auth: Authentication): Promise<Team[]> {
    const fetchOptions: RequestInit = {
        method: "GET",
        headers: {
            "Authorization" : `Bearer ${auth.access_token}`
        }
    }

    const fetchResponse = await fetch("/api/teams/owned", fetchOptions);

    if (!fetchResponse.ok)
        throw new Error(`${fetchResponse.status} - ${fetchResponse.statusText}`)
    return fetchResponse.json();
}

export async function getTeamMembers() {}