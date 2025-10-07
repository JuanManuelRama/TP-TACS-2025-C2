import type { StateCreator } from "zustand";

interface UserInformation {
	username: string;
	email: string;
	role?: "ADMIN" | "USER";
}

export interface AuthState {
	isAuthenticated: boolean;
	setIsAuthenticated: (isAuthenticated: boolean) => void;

	accessToken: string | undefined;
	setAccessToken: (accessToken: string) => void;

	reset: () => void;

	userInformation: UserInformation | undefined;
	setUserInformation: (userInformation: UserInformation) => void;
}

const createAuthSlice: StateCreator<AuthState> = (set) => ({
	isAuthenticated: false,
	setIsAuthenticated: (isAuthenticated) => set({ isAuthenticated }),

	accessToken: undefined,
	setAccessToken: (accessToken) => set({ accessToken }),

	userInformation: undefined,
	setUserInformation: (userInformation) => set({ userInformation }),

	reset: () =>
		set({
			isAuthenticated: false,
			accessToken: undefined,
			userInformation: undefined,
		}),
});

export default createAuthSlice;
