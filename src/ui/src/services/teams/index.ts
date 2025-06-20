import {Authentication} from "../../models/Auth.ts";
import {ResponseWrapper} from "../../models/ResponseWrapper.ts";
import {Team} from "../../models/Team.ts";

export async function getUserTeams(auth: Authentication) {
    const fetchOptions: RequestInit = {
        method: "GET",
        headers: {
            "Authorization" : `Bearer ${auth.access_token}`
        }
    }
    return fetch("/api/teams/owned", fetchOptions)
        .then(response => {
            if (response.ok){
                return response.json() as unknown as ResponseWrapper<Team[]>
            }
            else
                throw new Error(response.statusText);
        }).then(teamsResponse => teamsResponse.content);
}