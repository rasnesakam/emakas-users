
export function generateRandomString(length: number = 16): string {
    const bytes = new Uint8Array(length);
    crypto.getRandomValues(bytes);
    return  Array.from(bytes, byte => byte.toString(16).padStart(2,'0')).join('');
}

export enum HashAlgorithm {
    SHA_256 = "SHA-256"
}

export async function hashString(message: string, algorithm: HashAlgorithm = HashAlgorithm.SHA_256): Promise<Uint8Array> {
    const encoder = new TextEncoder();
    const data = encoder.encode(message);
    const hashBuffer = await crypto.subtle.digest(algorithm, data);
    return new Uint8Array(hashBuffer);
}

export function encodeBase64Url(binary: Uint8Array): string {
    const message = String.fromCharCode(...binary);
    return btoa(message)
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/, "")
}