import {User} from "@models/User.ts";

// Deprecated
export async function getUserInvitationLink(user: User): Promise<string> {
    return Promise.resolve(user).then(() => "Here is the link");
}

export async function inviteUser(user: User): Promise<User> {
    return Promise.resolve(user);
}
