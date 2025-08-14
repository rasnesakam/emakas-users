import {OAuthRequest} from "@models/OAuth.ts";
import {OAuthRequestKeys} from "@utils/enums/OAuthEnums.ts";
import {getSelfApplicationInfo} from "@services/applications";
import {getState} from "@utils/stateHolder";
import {getCodeChallenge} from "@utils/codeChallengeHolder";


export function initializeOAuthRequest(searchParams: URLSearchParams): OAuthRequest {

    const client_id = searchParams.get(OAuthRequestKeys.CLIENT_ID);
    const redirect_uri = searchParams.get(OAuthRequestKeys.REDIRECT_URI);
    const response_type = searchParams.get(OAuthRequestKeys.RESPONSE_TYPE);
    const scope = searchParams.getAll(OAuthRequestKeys.SCOPE);
    const state = searchParams.get(OAuthRequestKeys.STATE);

    if (response_type !== "code")
        throw new Error(`Field ${OAuthRequestKeys.RESPONSE_TYPE} should be set as code`);
    if (!client_id)
    {
        throw new Error(`Field ${OAuthRequestKeys.CLIENT_ID} is required`);
    }
    return {
        client_id,
        redirect_uri: redirect_uri!,
        response_type,
        scope: scope ?? undefined,
        state: state ?? undefined
    }
}


export async function getSelfApplicationOAuthRequest(): Promise<OAuthRequest> {
    const {client_id, scopes, redirectUri} = await getSelfApplicationInfo()

    return {
        client_id,
        redirect_uri: redirectUri,
        scope: scopes,
        response_type: "code",
        state: getState(),
        code_challenge: await getCodeChallenge()
    }
}