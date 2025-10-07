import { AxiosError } from "axios";
import { useState } from "react";
import { useNavigate } from "react-router";
import { toast } from "sonner";
import { register } from "../api/authService";
import type { SignupFormValues } from "../routes/auth/signup/form/schema";
import useBoundStore from "../store";

const useSignup = () => {
	const [error, setError] = useState<string | undefined | unknown>(undefined);
	const [loading, setLoading] = useState(false);
	const { setIsAuthenticated, setAccessToken, setUserInformation } =
		useBoundStore();
	const navigate = useNavigate();

	const signup = async (payload: SignupFormValues) => {
		setLoading(true);
		setError(undefined);
		try {
			const { email, password } = payload;
			const data = await register({ username: email, password });

			setLoading(false);
			if (data) {
				setIsAuthenticated(true);
				setAccessToken(data.token);
				setUserInformation({
					username: data.user.username,
					email: data.user.username,
					id: data.user.id,
				});
				toast.success("Account created", {
					description: "Account created successfully " + data.user.username,
				});
				navigate("/events", { replace: true });
			}
		} catch (error) {
			console.log(error);
			if (error instanceof AxiosError) {
				setError(error.response?.data.message);
			}
			console.log(JSON.stringify(error));
			setError("Error");
			setLoading(false);
		}
	};

	return {
		signup,
		loading,
		error,
	};
};

export default useSignup;
