import axios from "axios"
import dotenv from "dotenv";
dotenv.config();

const axiosInstance = axios.create({
    baseURL: process.env.API_BASE_URL,
    timeout: 1000
});

axiosInstance.interceptors.request.use((config) => {
  if (config.jwt) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${config.jwt}`;
    delete config.jwt; // clean up to avoid leaking it downstream
  }
  return config;
});

export default axiosInstance;