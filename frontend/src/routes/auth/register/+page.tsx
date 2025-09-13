import { useState, FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import "./+page.css";
import { login, register } from "@/api/users.ts";

const Page = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    async function handleRegistration(e: FormEvent) {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await register(username.trim(), password.trim());
            const data = await login(username.trim(), password.trim());
            const token = data.token;

            localStorage.setItem("jwt", token);
            localStorage.setItem("username", username.trim());
            navigate("/events");
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError("Registration failed, please try again");
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <form className="register-container" onSubmit={handleRegistration}>
            <h2>Register</h2>
            <div>
                <label htmlFor="username">Username:</label>
                <input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Enter your username"
                    disabled={loading}
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
                    disabled={loading}
                />
            </div>
            <button
                type="submit"
                disabled={loading}
                className={`transition-opacity duration-300 ${
                    loading ? "opacity-50 cursor-not-allowed" : "opacity-100"
                }`}>
                Register
            </button>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </form>
    );
};

export { Page as Component };
export default Page;