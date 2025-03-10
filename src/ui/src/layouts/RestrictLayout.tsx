import {Outlet} from "react-router";
import {AppLayout} from "@layouts/AppLayout.tsx";

export function RestrictLayout() {

    return <>
        <AppLayout>
            <Outlet/>
        </AppLayout>
    </>
}