/* ------------------------------ events • routes.tsx -- */

/* ---------------------------------------------- routes -- */

export const route: RouteObject = {
	path: "/events",
	// element: <ProtectedRoute />,
	children: [
		{
			path: "",
			lazy: () => import("./+page"),
		},
		{
			path: ":id",
			lazy: () => import("./[id]/+page"),
		},
		{
			path: "new",
			lazy: () => import("./+new"),
		},
	],
};

/* ----------------------------------------------- types -- */

import type { RouteObject } from "react-router";
