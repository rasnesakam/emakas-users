import {createContext, useContext, useState} from "react";
import {Authentication} from "../../models/Auth.ts";
import {registerLocalAuthentication, retrieveLocalAuthentication} from "@services/auth";
import {JSX} from "react/jsx-runtime";

interface AuthContextParams {
    auth: Authentication | undefined;
    setAuth: (auth: Authentication | undefined) => void;
}

const AuthContext = createContext<AuthContextParams>({
    auth: undefined,
    setAuth: () => {}
});

function AuthContextAdapter ({children}: {children: JSX.Element}) {
    const [auth, setAuth] = useState<Authentication | undefined>(retrieveLocalAuthentication());
    const setPersistedAuth = (auth: Authentication | undefined) => {
        if (auth){
            registerLocalAuthentication(auth);
            setAuth(auth);
        }
    }
    return <>
        <AuthContext.Provider value={{auth, setAuth: setPersistedAuth}}>
            {children}
        </AuthContext.Provider>
    </>
}

function useAuthContext() {
    return useContext(AuthContext);
}

export {type AuthContextParams, AuthContextAdapter, useAuthContext}