import { Navigate, type RouteObject } from "react-router";

export const route: RouteObject = {
	path: "/auth",
	children: [
		{
			index: true,
			element: <Navigate to="login" replace />,
		},
		{
			path: "login",
			lazy: () => import("./login/+page.tsx"),
		},
		{
			path: "register",
			lazy: () => import("./register/+page.tsx"),
		},
	],
};
