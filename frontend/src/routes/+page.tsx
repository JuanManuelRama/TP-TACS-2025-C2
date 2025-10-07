/* --------------------------------------------------- ⫻ -- */

import { useEffect } from "react";
import { useNavigate } from "react-router";
import useBoundStore from "../store";

//const isDev = process.env.NODE_ENV === "development"; I didn't understand what it was supposed to do

/* ------------------------------------------- component -- */

export const Page = () => {
	const navigate = useNavigate();
	const { isAuthenticated } = useBoundStore();

	useEffect(() => {
		if (isAuthenticated) {
			navigate("/events", { replace: true }); // for now since dashboard is empty
		} else {
			navigate("/auth/login", { replace: true });
		}
	}, [navigate, isAuthenticated]);

	return null;
};

export { Page as Component }; //a
export default Page; //b

/* --------------------------------------------- comments -- ;

    This is the root page, in production this component will
    trigger a redirect to /dashboard or /signin depending on
    the authentication state.

    [a] react-router-dom expects the export to be named as
    {Component}.

    [b] for efficient HMR, always export one component per
    file and it should be a default export.

/* -------------------------------------------------------- */
