import {useSearchParams} from "react-router";

export function ErrorPage() {
    const [searchParams] = useSearchParams()
    return <>
        <h1>{searchParams.get("title")}</h1>
        <p>{searchParams.get("description")}</p>
    </>
}