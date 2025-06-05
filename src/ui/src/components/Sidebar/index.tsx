import { ClassAttributes, HTMLAttributes } from "react";
import { JSX } from "react/jsx-runtime";

export function Sidebar(props: JSX.IntrinsicAttributes & ClassAttributes<HTMLDivElement> & HTMLAttributes<HTMLDivElement>) {

    return <div {...props}>
        <div>
            <span className="text-3xl font-bold">
                emakas-IAM
            </span>
        </div>
        <ul className="mt-4">
            <li>Link1</li>

            <li>Link1</li>

            <li>Link1</li>

            <li>Link1</li>
        </ul>
    </div>
}