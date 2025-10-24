import axios from "axios";
import { getAuthState } from "../store/index.ts";
import HttpError from "./HttpError.ts";

const axiosInstance = axios.create({
	baseURL: import.meta.env.VITE_API_URL || "/api",
});

axiosInstance.interceptors.response.use(
	(response) => response,
	(error) => {
		if (axios.isAxiosError(error) && error.response) {
			return Promise.reject(
				new HttpError(
					error.response.status,
					error.response.data?.error || error.message,
				),
			);
		}
		return Promise.reject(error);
	},
);

axiosInstance.interceptors.request.use((config) => {
	const { accessToken } = getAuthState();
	if (accessToken) {
		config.headers.Authorization = `Bearer ${accessToken}`;
	}
	return config;
});

export default axiosInstance;
