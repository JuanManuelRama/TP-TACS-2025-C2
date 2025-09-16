import { Button } from "$components/ui/button.tsx";
import { Outlet, useNavigate } from "react-router";

const Header = ({ isLoggedIn, username, handleLogout }: any) => {
    const navigate = useNavigate();
    const AuthButtons = () => (
        <div className="flex items-center gap-2">
            <Button variant="outline" onClick={() => navigate("/auth/register")}>Register</Button>
            <Button variant="outline" onClick={() => navigate("/auth/login")}>Log In</Button>
        </div>
    );

    const UserInfo = () => (
        <div className="flex items-center gap-2">
            <span>{username}</span>
            <Button variant="outline" onClick={handleLogout}>Logout</Button>
        </div>
    );
    return (
        <header className="h-14 border-b border-gray-200 flex items-center px-5">
            <span>Event Manager</span>
            <div className="ml-auto flex gap-2">
                {isLoggedIn ? <UserInfo /> : <AuthButtons />}
            </div>
        </header>
    );
};

const Layout = ({ children }: { children?: React.ReactNode }) => {
    const navigate = useNavigate();
    const isLoggedIn = !!localStorage.getItem("jwt"); // example
    const username = localStorage.getItem("username") || "";


    const handleLogout = () => {
        localStorage.removeItem("jwt");
        localStorage.removeItem("username");
        localStorage.removeItem("id");
        navigate("/auth/login");
    };

    return (
        <div className="h-screen">
            <Header
                isLoggedIn={isLoggedIn}
                username={username}
                handleLogout={handleLogout}
            />
            <main className="mt-10 mx-40">{children || <Outlet />}</main>
        </div>
    );
};

export default Layout;