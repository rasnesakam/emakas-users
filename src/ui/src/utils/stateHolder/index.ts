import {generateRandomString} from "@utils/crypto";

const SESSION_STATE_KEY = "session-state";

function setSessionState(): string {
    const randomString = generateRandomString();
    window.sessionStorage.setItem(SESSION_STATE_KEY, randomString);
    return randomString;
}

export function getState(): string {
    let persistedState = window.sessionStorage.getItem(SESSION_STATE_KEY);
    if (!persistedState)
        persistedState = setSessionState();
    return persistedState;
}

