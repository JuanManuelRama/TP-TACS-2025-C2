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
	],
};

/* ----------------------------------------------- types -- */

import type { RouteObject } from "react-router";
