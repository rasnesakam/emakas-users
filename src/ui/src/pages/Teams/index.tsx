import {useEffect, useState} from "react";
import {Team} from "@models/Team.ts";
import {useAuthContext} from "@contexts/AuthContext";
import {getUserTeams} from "@services/teams";
import {Accordion, AccordionContent, AccordionItem, AccordionTrigger} from "@components/shadcn/ui/accordion.tsx";
import {Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@components/shadcn/ui/card"
import {Button} from "@components/shadcn/ui/button";
import {Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow} from "@components/shadcn/ui/table"

export function TeamsPage() {
    const [teams, setTeams] = useState<Team[]>([]);
    const {auth} = useAuthContext();
    useEffect(() => {
        getUserTeams(auth!).then(fetchedTeams => setTeams(fetchedTeams))
    }, []);
    return <>
        <div className="max-w-[1920px] mx-auto p-10">
            <Card>
                <CardHeader>
                    <CardTitle className="text-left">My Teams</CardTitle>
                    <CardDescription className="text-left">The teams that I am lead of or those that sub teams of my teams</CardDescription>
                    <CardAction>
                        <Button variant="link">Create New Team</Button>
                    </CardAction>
                </CardHeader>
                <CardContent>
                    <Accordion type="multiple">
                        {teams.map((team, index) =><AccordionItem key={`team-${index}`} value={`team-${index}`}>
                            <AccordionTrigger>
                                <div className="text-xl font-bold">{team.name}</div>
                            </AccordionTrigger>
                            <AccordionContent>
                                <div className="flex flex-col justify-start items-start">
                                    <div className="w-full">
                                        <span className="font-bold mr-2">Team Lead</span>
                                        <span>{team.lead.full_name}</span>
                                    </div>
                                    <div className="w-full">
                                        <Table>
                                            <TableCaption>Members of This Team</TableCaption>
                                            <TableHeader>
                                                <TableRow>
                                                    <TableHead>Full Name</TableHead>
                                                    <TableHead>E mail</TableHead>
                                                </TableRow>
                                            </TableHeader>
                                            <TableBody>
                                                {team.members.map((member, memberIndex) => <TableRow key={`member-${team.uri}-${memberIndex}`}>
                                                    <TableCell>
                                                        {member.full_name}
                                                    </TableCell>
                                                    <TableCell>
                                                        {member.email}
                                                    </TableCell>
                                                </TableRow>)}
                                            </TableBody>
                                        </Table>
                                    </div>
                                </div>
                            </AccordionContent>
                        </AccordionItem>)}
                    </Accordion>
                </CardContent>
                <CardFooter>
                    <p>There are {teams.length} teams found</p>
                    <Button>Invite New Member</Button>
                </CardFooter>
            </Card>
        </div>
    </>
}