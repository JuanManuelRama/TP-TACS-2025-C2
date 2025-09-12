import {User} from "$types/user.ts";

export interface Event {
    id: string,
    organizador: User
    titulo: string;
    descripcion: string;
    inicio: string;
    duracion: number;
    cupoMaximo: number;
    cupoMinimo: number | null;
    precio: number;
    categoria: string;
}

export interface EventList {
    data: Event[];
    page: number;
    pageSize: number;
    total: number;
}


// export interface AddEventRequest {
// }
// export interface UpdateEventRequest {}