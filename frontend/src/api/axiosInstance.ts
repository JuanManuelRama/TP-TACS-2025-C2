import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080/",
})

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        throw error;
    }
)

axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("jwt");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default axiosInstance;