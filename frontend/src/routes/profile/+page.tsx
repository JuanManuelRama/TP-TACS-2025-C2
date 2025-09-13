import { useEffect } from "react";
import { useNavigate } from "react-router";

const Page = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("jwt");
        if (!token) {
            navigate("/auth", { replace: true });
        }
    }, [navigate]);

    return <div>Perfil de {localStorage.getItem("username")}</div>;
};
export { Page as Component };
export default Page;
