// import useBoundStore from "@/store";

import { signIn, signOut } from "@/api/authService";
import { AxiosError } from "axios";
import { useState } from "react";
import { useNavigate } from "react-router";
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
	// const { setAuthentication, clearAuthentication } = useBoundStore();

	const navigate = useNavigate();

	const login = async (username: string, password: string): Promise<void> => {
		try {
			setLoading(true);
			const response = await signIn(username, password);
			setLoading(false);
			console.log(response.data.token);
			localStorage.setItem("jwt", response.data.token);
			localStorage.setItem("username", response.data.user.username);
			localStorage.setItem("id", response.data.user.id);
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
		// queryClient.clear();
		// queryClient.invalidateQueries();
		localStorage.removeItem("jwt");
		localStorage.removeItem("username");
		localStorage.removeItem("id");
		// clearAuthentication();

		/* sessionStorage.removeItem("domainId"); */
		// toast.success("loggedOut");
	};

	return {
		login,
		logout,
		error,
		loading,
	};
};

export default useAuth;
