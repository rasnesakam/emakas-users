import {User} from "@models/User.ts";

export interface Team {
    id?: string;
    name: string;
    description: string;
    uri: string;
    parent_team: Team;
    child_teams: Team[];
    members: User[];
    lead: User
}