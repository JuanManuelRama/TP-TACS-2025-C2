import type { RouteObject } from "react-router";

export const route: RouteObject = {
	path: "/auth",
	children: [
		{
			path: "",
			lazy: () => import("./+page"),
		},
	],
};
