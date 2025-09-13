import {User} from "$types/user.ts";

export interface Registration {
    usuario: User;
    horaInscripcion: string;
    tipo: RegistrationType;
}

type RegistrationType = "CONFIRMACION" | "ESPERA";