import axios from "axios";
import HttpError from "./HttpError.ts";



const axiosInstance = axios.create({
    baseURL: "http://localhost:8080/",
})

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (axios.isAxiosError(error) && error.response) {
            return Promise.reject(new HttpError(
                error.response.status,
                error.response.data?.error || error.message
            ));
        }
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default axiosInstance;