import {User} from "@models/User.ts";

export function getUserFromFormData(formData: FormData): User {
    return {
        name: formData.get("name")!.toString(),
        surname: formData.get("surname")!.toString(),
        email: formData.get("email")!.toString(),
        username: formData.get("username")!.toString(),
        full_name: `${formData.get("name")} ${formData.get("surname")}`
    }
}