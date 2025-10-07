import type { NewEvent } from "../types/event";
import axiosInstance from "./axiosInstance";

export const eventList = () => {
	return axiosInstance.get("/eventos").then((res) => res.data);
};

export const event = (eventId: string) => {
	return axiosInstance.get(`/eventos/${eventId}`).then((res) => res.data);
};

export const addEvent = (event: NewEvent) => {
	return axiosInstance.post("/eventos", event).then((res) => res.data);
};

export const deleteEvent = (eventId: string) => {
	return axiosInstance.delete(`/eventos/${eventId}`).then((res) => res.data);
};

export const subscribeEvent = (eventId: string) => {
	return axiosInstance
		.post(`/eventos/${eventId}/inscriptos`)
		.then((res) => res.data);
};

export const unsubscribeEvent = (eventId: string) => {
	return axiosInstance
		.delete(`/eventos/${eventId}/inscriptos`)
		.then((res) => res.data);
};

export const suscriptionEvent = (eventId: string, userId: string) => {
	return axiosInstance
		.get(`/eventos/${eventId}/inscriptos/${userId}`)
		.then((res) => res.data);
};

export const kickEvent = (eventId: string, userId: string) => {
	return axiosInstance.delete(`/eventos/${eventId}/inscriptos/${userId}`, {});
};
