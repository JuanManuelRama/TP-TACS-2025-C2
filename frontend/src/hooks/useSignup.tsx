import { useState } from "react";
import { register } from "../api/authService";
import type { SignupFormValues } from "../routes/auth/signup/form/schema";

const useSignup = () => {
	const [error, setError] = useState<string | undefined | unknown>(undefined);
	const [loading, setLoading] = useState(false);

	const signup = async (payload: SignupFormValues) => {
		setLoading(true);
		try {
			const { email, password } = payload;
			const data = await register({ username: email, password });

			if (data) {
				console.log(data);
			}
		} catch (error) {
			console.log(error);
			setError(error);
		}
	};

	return {
		signup,
		loading,
		error,
	};
};

export default useSignup;
