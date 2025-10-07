import ProtectedRoute from "$/src/lib/protected-route";
import { Suspense } from "react";
import { Outlet, type RouteObject } from "react-router";

export const route: RouteObject = {
	path: "/profile",
	element: (
		<>
			<Suspense>
				<ProtectedRoute>
					<Outlet />
				</ProtectedRoute>
			</Suspense>
		</>
	), //a
	children: [
		{
			path: "",
			lazy: () => import("./+page"),
		},
	],
};
