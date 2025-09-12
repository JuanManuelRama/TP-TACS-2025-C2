import {User} from "$types/user.ts";

export interface Registration {
    usuario: User;
    horaInscripcion: string;
    tipo: RegistrationType;
}

enum RegistrationType {
    CONFIRMACION  = "CONFIRMACION",
    ESPERA = "ESPERA",
}