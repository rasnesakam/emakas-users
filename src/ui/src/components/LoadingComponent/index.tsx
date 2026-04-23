import styles from "./index.module.css"

export interface LoadingComponentArg {
    active: boolean;
}
export function LoadingComponent({active}: LoadingComponentArg){

    return <div style={{display: active ? "block" : "none"}}>
        <div className="overflow-hidden relative h-2">
            <div className={"w-full absolute t-0 absolute t-0 " + styles.animTranslateHorizontal1}>
                <div className="h-2 bg-primary w-2 rounded-full"></div>
            </div>

            <div className={"w-full absolute t-0 " + styles.animTranslateHorizontal2}>
                <div className="h-2 bg-primary w-2 rounded-full"></div>
            </div>

            <div className={"w-full absolute t-0 " + styles.animTranslateHorizontal3}>
                <div className="h-2 bg-primary w-2 rounded-full"></div>
            </div>

            <div className={"w-full absolute t-0 " + styles.animTranslateHorizontal4}>
                <div className="h-2 bg-primary w-2 rounded-full"></div>
            </div>

            <div className={"w-full absolute t-0 " + styles.animTranslateHorizontal5}>
                <div className="h-2 bg-primary w-2 rounded-full"></div>
            </div>
        </div>
    </div>
}