import axiosInstance from "./axiosInstance"


export const eventList = () => {
    return axiosInstance.get("/events").then(res => res.data)
}

export const event = (eventId: string) =>{
    return axiosInstance.get(`/events/${eventId}`).then(res => res.data)
}
interface Event {
    id: string
    name: string
    description: string
    date: string
    location: string
}

export const addEvent = (event: Event) => {
    return axiosInstance.post("/events", event).then(res => res.data)
}

export const deleteEvent = (eventId:string ) => {
    return axiosInstance.delete(`/events/${eventId}`).then(res => res.data)
}

export const subscribeEvent = (eventId:string ) => {
    return axiosInstance.post(`/events/${eventId}/subscribe`).then(res => res.data)
}

export const unsubscribeEvent = (eventId:string ) => {
    return axiosInstance.post(`/events/${eventId}/unsubscribe`).then(res => res.data)
}

