/* ------------------------------------------- router.tsx -- ;
           _
     .____|=|_________.      This is the router for vite and   
    /__________________\     vitest. Application router acts
     ||  || .--. ||  ||      as an accumulator and origin for 
     ||[]|| | .| ||[]||      all the routes in the project. 
     ||__||_|__|_||__||      AxiosManager is experimental.

/* -------------------------------------------------------- */

import { createBrowserRouter, Outlet } from "react-router";

import { routes as root } from "$routes/+routes";

if (import.meta.hot) {
	import.meta.hot.dispose(() => {
		if (router) {
			router.dispose();
		}
	});
}

/* ---------------------------------------------- router -- */

const routes: RouteObject[] = [
	{
		path: "/",
		element: <Outlet />,
		children: root,
	},
];

const router = createBrowserRouter(routes, {
	future: {
		v7_relativeSplatPath: true,
	},
});

export { router, routes };
export default router;

import type { RouteObject } from "react-router";
/* ----------------------------------------------- types -- */
