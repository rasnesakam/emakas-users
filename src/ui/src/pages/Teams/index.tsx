import {useEffect, useState} from "react";
import {Team} from "@models/Team.ts";
import {useAuthContext} from "@contexts/AuthContext";
import {getUserTeams} from "@services/teams";

export function TeamsPage() {
    const [teams, setTeams] = useState<Team[]>([]);
    const {auth} = useAuthContext();
    useEffect(() => {
        getUserTeams(auth!).then(fetchedTeams => setTeams(fetchedTeams))
    }, []);
    return <>
        {teams.map((item, index) => <div key={`team-${index}`}>{item.name}</div>)}
    </>
}