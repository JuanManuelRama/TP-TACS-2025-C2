import {useEffect, useState} from "react";
import {Registration} from "@/types/registration.ts";
import {Event} from "@/types/event.ts";
import axiosInstance from "@/api/axiosInstance.ts";
import {suscriptionEvent} from "@/api/events.ts";
import HttpError from "@/api/HttpError.ts";

export const useRegistrations = (eventId: string) => {
    const [confirmed, setConfirmed] = useState<Registration[]>([]);
    const [waitlisted, setWaitlisted] = useState<Registration[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!eventId) return;

        const fetchRegistrations = async () => {
            setLoading(true);
            setError(null);

            try {
                const res = await axiosInstance.get<Registration[]>(
                    `/eventos/${eventId}/inscriptos`
                );

                const data = res.data;

                setConfirmed(data.filter(r => r.tipo === "CONFIRMACION"));
                setWaitlisted(data.filter(r => r.tipo === "ESPERA"));
            } catch (err: unknown) {
                if (err instanceof Error) setError(err.message);
                else setError("Unknown error");
            } finally {
                setLoading(false);
            }
        };

        fetchRegistrations();
    }, [eventId]);

    return { confirmed, setConfirmed, waitlisted, setWaitlisted, loading, error };
};


/**
 * no es la solución más limpia, 404 significa que el endpoint no encontro la inscripción, por ende
 * no estaba inscripto, 401 es que la jwt no era válida, por lo que tampoco se puede inscribir.
 *
 * Otra solución podria ser crear el endpoint eventos/:id/is-registered` que responda true o false dependiendo
 * de si lo encuentra o no. Esta solución es menos restfull, aunque más cómoda para le front, y tampoco
 * soluciona el problema de recibir 401 si el token estaba expirada, para usuarios no logueados se los
 * puede redirigir desde el botón en sí
 * */
export function useSubscription(event: Event | null) {
    const [isSubscribed, setSubscribed] = useState<boolean | null>(null);

    useEffect(() => {
        if (!event) return; // wait until event is loaded
        const userId = localStorage.getItem("id");
        if (!userId || userId === event.organizador.id) {
            setSubscribed(false);
            return;
        }

        const checkSubscription = async () => {
            try {
                await suscriptionEvent(event.id, userId);
                setSubscribed(true);
            } catch (err: unknown) {
                if (err instanceof HttpError && (err.status === 404 || err.status === 401)) {
                    setSubscribed(false);
                } else {
                    setSubscribed(false);
                }
            }
        };

        checkSubscription();
    }, [event]);

    return { isSubscribed, setSubscribed };
}