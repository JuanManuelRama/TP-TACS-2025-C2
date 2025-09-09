export interface Event {
    id: string,
    description: string
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