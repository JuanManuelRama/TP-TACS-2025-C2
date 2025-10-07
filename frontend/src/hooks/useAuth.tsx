import { signIn, signOut } from "@/api/authService";
import { AxiosError } from "axios";
import { useState } from "react";
import { useNavigate } from "react-router";
import { toast } from "sonner";
import useBoundStore from "../store";

export interface TokenResponse {
	accessToken: string;
	refreshToken: string;
	systemIdentifier: string;
	bannerLogoImage: string;
}

const useAuth = () => {
	const [error, setError] = useState<undefined | unknown>("");
	const [loading, setLoading] = useState(false);
	const { setIsAuthenticated, setAccessToken, setUserInformation, reset } =
		useBoundStore();
	// const { setAuthentication, clearAuthentication } = useBoundStore();

	const navigate = useNavigate();
	

	const login = async (username: string, password: string): Promise<void> => {
		try {
			setLoading(true);
			setError(undefined)
			const response = await signIn(username, password);
			setLoading(false);
			toast.success("Logged In",{
				description: "Logged in as " + username,
			});
			setIsAuthenticated(true);
			setAccessToken(response.data.token);
			setUserInformation({
				username: response.data.user.username,
				email: response.data.user.username,
				role: "USER",
				id: response.data.user.id
			});
			navigate(`/events`, { replace: true });
		} catch (requestError: unknown) {
			setLoading(false);
			if (requestError instanceof AxiosError) {
				setError(requestError.response?.data.message);
			}else{
				setError("An error ocurred")
			}
			
		}
	};

	const logout = async () => {
		try {
			await signOut();
			toast.success("Logged out.")
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
