import type { ReactElement } from "react";
import { Outlet } from "react-router";

const Layout = ({ children }: { children?: ReactElement }) => {
	return (
		<div className="flex flex-1">
			<main>{children || <Outlet />}</main>
		</div>
	);
};

export default Layout;
