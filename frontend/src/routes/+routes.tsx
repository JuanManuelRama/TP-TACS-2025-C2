import { Suspense } from "react";
import type { RouteObject } from "react-router";
import _50X from "./+50X.tsx";
import Layout from "./+layout.tsx";
import { route as auth } from "./auth/+route.tsx";
import { route as events } from "./events/+route.tsx";
import { route as profile } from "./profile/+route.tsx";

export const routes: RouteObject[] = [
	{
		path: "/",
		element: (
			<>
				<Suspense>
					<Layout />
				</Suspense>
			</>
		), //a

		errorElement: <_50X />,
		children: [
			{
				path: "",
				lazy: () => import("./+page"),
			},
			{
				path: "*",
				lazy: () => import("./+404.tsx"),
			},
			events,
			profile,
		],
	},
	auth,
];
