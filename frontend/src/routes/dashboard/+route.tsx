import type { RouteObject } from "react-router";

export const route: RouteObject = {
	path: "/dashboard",
	children: [
		{
			path: "",
			lazy: () => import("./+page"),
		},
	],
};
