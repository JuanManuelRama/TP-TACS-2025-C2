import type { RouteObject } from "react-router";

export const route: RouteObject = {
	path: "/profile",
	children: [
		{
			path: "",
			lazy: () => import("./+page"),
		},
	],
};
