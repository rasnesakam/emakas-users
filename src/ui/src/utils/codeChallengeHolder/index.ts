import {generateRandomString, hashString} from "@utils/crypto";

const CODE_VERIFIER_KEY = "session-code-verifier"
const CODE_CHALLENGE_KEY = "session-code-challenge"

function setSessionItem(key: string, value: string): string {
    sessionStorage.setItem(key,value);
    return value;
}

/**
 * Code Verifier is plain random string
 */
export function getCodeVerifier(): string {
    let codeVerifier = sessionStorage.getItem(CODE_VERIFIER_KEY);
    if (!codeVerifier)
        codeVerifier = setSessionItem(CODE_VERIFIER_KEY, generateRandomString());
    return codeVerifier;
}

/**
 * Code challenge is hashed version of code verifier
 */

export async function getCodeChallenge(): Promise<string> {
    const codeVerifier = getCodeVerifier();
    let challenge = sessionStorage.getItem(CODE_CHALLENGE_KEY);
    if (!challenge)
        challenge = setSessionItem(CODE_CHALLENGE_KEY, await hashString(codeVerifier));
    return challenge;
}