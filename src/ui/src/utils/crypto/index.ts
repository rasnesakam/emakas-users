
export function generateRandomString(length: number = 16): string {
    const bytes = new Uint8Array(length);
    crypto.getRandomValues(bytes);
    return  Array.from(bytes, byte => byte.toString(16).padStart(2,'0')).join('');
}

export enum HashAlgorithm {
    SHA_256 = "SHA-256"
}

export async function hashString(message: string, algorithm: HashAlgorithm = HashAlgorithm.SHA_256): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(message);
    const hashBuffer = await crypto.subtle.digest(algorithm, data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2,'0')).join('')
}