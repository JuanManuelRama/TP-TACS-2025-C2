import axiosInstance from "./axiosInstance"

export const login = (username: string, password: string) => {
    return axiosInstance
        .post("/usuarios/login", { username, password }) // send as JSON body
        .then(res => res.data);
};

export const register = (username: string, password: string) => {
    return axiosInstance
        .post("/usuarios", { username, password , type: "PARTICIPANTE" })
        .then(res => res.data);
}

export const getEvents = () => {
    return axiosInstance
        .get("usuarios/eventos")
        .then(res => res.data);
}