import type { Event } from "./event";
export interface User {
    id: string;
    username: string;
    type: string;
}

export interface UserEvents {
    eventosCreados: Event[]
    eventosConfirmados: Event[]
    eventosEnEspera: Event[]
}