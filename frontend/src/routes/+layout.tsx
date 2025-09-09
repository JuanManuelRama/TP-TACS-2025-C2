import type { ReactElement } from "react";
import { Outlet } from "react-router";
import { Button } from "../components/ui/button";

const Layout = ({ children }: { children?: ReactElement }) => {
	return (
		<div className="h-screen">
			<header className="h-14 border-b border-gray-200 flex flex-row items-center justify-start px-5">
				<span>Event Manager</span>
				<Button className="ml-auto" variant="outline">
					Log In
				</Button>
			</header>
			<main className="mt-10 mx-40">{children || <Outlet />}</main>
		</div>
	);
};

export default Layout;
