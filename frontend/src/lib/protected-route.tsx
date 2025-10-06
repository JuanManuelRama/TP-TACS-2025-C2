import { type ReactNode, useState } from "react";
import { Navigate, Outlet } from "react-router";

// import useBoundStore from "../store";

const ProtectedRoute = ({ children }: { children: ReactNode }) => {
	// const navigate = useNavigate();
	const [isAuthenticated, setIsAuthenticated] = useState(false);
	// const { isAuthenticated, clearAuthentication } = useBoundStore();
	// const signinPath = "/auth";

	// useEffect(() => {
	// 	if (!isAuthenticated()) {
	// 		clearAuthentication();
	// 		navigate(signinPath, { replace: true });
	// 	}
	// }, [isAuthenticated()]);

	return isAuthenticated ? (
		children || <Outlet />
	) : (
		<Navigate to="/auth" replace />
	);
};

export default ProtectedRoute;
