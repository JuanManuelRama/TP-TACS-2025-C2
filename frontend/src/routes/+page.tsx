/* --------------------------------------------------- ⫻ -- */

import { useNavigate } from "react-router";

const isDev = process.env.NODE_ENV === "development";

/* ------------------------------------------- component -- */

export const Page = () => {
    
    const navigate = useNavigate();

    navigate(isDev ? "/events" : "/events", { replace: true });

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
