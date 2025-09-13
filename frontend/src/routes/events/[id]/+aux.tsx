import {useEffect, useState} from "react";
import {Registration} from "$types/registration.ts";
import axiosInstance from "@/api/axiosInstance.ts";

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
