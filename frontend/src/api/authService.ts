import axiosInstance from "./axiosInstance";

export const signIn = (username: string, password: string) => {
	return axiosInstance
		.post<{
			token: string;
			user: {
				id: string;
				username: string;
			};
		}>("/usuarios/login", {
			username,
			password,
		})
		.then((response) => response);
};

export const signOut = () => {
	return axiosInstance.post("/usuarios/logout");
};

export const register = (payload: { username: string; password: string }) => {
	const { username, password } = payload;
	return axiosInstance
		.post("/usuarios", {
			username,
			password,
			type: "PARTICIPANTE",
		})
		.then((response) => response.data);
};
