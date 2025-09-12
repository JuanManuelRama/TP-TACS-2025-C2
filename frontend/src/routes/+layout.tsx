import type { ReactElement } from "react";
import { Outlet, useNavigate } from "react-router";
import { Button } from "../components/ui/button";

const Layout = ({ children }: { children?: ReactElement }) => {
	const navigate = useNavigate();
    const username = localStorage.getItem("username");
    const isLoggedIn = !!username;
	return (
		<div className="h-screen">
			<header className="h-14 border-b border-gray-200 flex flex-row items-center justify-start px-5">
				<span>Event Manager</span>
				<Button
					className="ml-auto"
					variant="outline"
                    onClick={() => navigate(isLoggedIn ? "/profile" : "/auth")}				>
                    {isLoggedIn ? username : "Log In"}
				</Button>
			</header>
			<main className="mt-10 mx-40">{children || <Outlet />}</main>
		</div>
	);
};

export default Layout;
