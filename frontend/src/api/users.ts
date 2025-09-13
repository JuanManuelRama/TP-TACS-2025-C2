import axiosInstance from "./axiosInstance"

export const login = (username: string, password: string) => {
    return axiosInstance
        .post("/usuarios/login", { username, password }) // send as JSON body
        .then(res => res.data);
};