import type { ReactNode } from "react";
import { Navigate, Outlet } from "react-router";
import useBoundStore from "../store";

const ProtectedRoute = ({ children }: { children: ReactNode }) => {
	const { isAuthenticated } = useBoundStore();

	return isAuthenticated ? (
		children || <Outlet />
	) : (
		<Navigate to="/auth" replace />
	);
};

export default ProtectedRoute;
