import { Suspense } from "react";
import type { RouteObject } from "react-router";

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
		],
	},
];