// import useBoundStore from "@/store";

import { signIn, signOut } from "@/api/authService";
import { AxiosError } from "axios";
import { useState } from "react";
import { useNavigate } from "react-router";
import useBoundStore from "../store";
// import { toast } from "sonner";

export interface TokenResponse {
	accessToken: string;
	refreshToken: string;
	systemIdentifier: string;
	bannerLogoImage: string;
}

const useAuth = () => {
	const [error, setError] = useState("");
	const [loading, setLoading] = useState(false);
	const { setIsAuthenticated, setAccessToken, setUserInformation, reset } =
		useBoundStore();
	// const { setAuthentication, clearAuthentication } = useBoundStore();

	const navigate = useNavigate();

	const login = async (username: string, password: string): Promise<void> => {
		try {
			setLoading(true);
			const response = await signIn(username, password);
			setLoading(false);
			setIsAuthenticated(true);
			setAccessToken(response.data.token);
			setUserInformation({
				username: response.data.user.username,
				email: response.data.user.username,
				role: "USER",
			});
			// if (response?.accessToken) {
			// 	const { token } = response.accessToken;
			// 	sessionStorage.setItem("token", token);
			// 	// setAuthentication(username, token);
			// }
			navigate(`/events`, { replace: true });
		} catch (requestError: unknown) {
			if (requestError instanceof AxiosError) {
				setError(requestError.response?.data.message);
				setLoading(false);
			}
		}
	};

	const logout = async () => {
		try {
			await signOut();
		} catch (error) {
			console.log(error);
		}

		reset();
	};

	return {
		login,
		logout,
		error,
		loading,
	};
};

export default useAuth;
