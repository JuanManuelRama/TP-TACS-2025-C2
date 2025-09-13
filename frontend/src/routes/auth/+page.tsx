import {useState, FormEvent} from "react";
import { useNavigate } from "react-router-dom";
import "./+page.css";
import {login} from "@/api/users.ts";

const Page = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    async function handleLogin(e: FormEvent) {
        e.preventDefault();
        setError(null);

        try {
            const data = await login(username, password);
            const token = data.token;
            localStorage.setItem("jwt", token);
            localStorage.setItem("username", username);
            navigate("/dashboard");

        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError("Login failed, please try again");
            }
        }
    }

    return (
        <form className="login-container" onSubmit={handleLogin}>
            <h2>Log In</h2>
            <div>
                <label htmlFor="username">Username:</label>
                <input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Enter your username"
                />
            </div>
            <div>
                <label htmlFor="password">Password:</label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                />
            </div>
            <button type="submit">Log In</button>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </form>
    );
};

export { Page as Component };
export default Page;
