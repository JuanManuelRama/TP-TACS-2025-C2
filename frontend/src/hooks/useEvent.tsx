import { useState, useEffect } from "react";
import { event as fetchEvent } from "@/api/events";
import { Event } from "@/types/event";

/**
 * returns everything null if the id is null
 * */
const useEvent = (eventId: string | null) => {
    const [event, setEvent] = useState<Event | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!eventId) {
            // No ID provided → skip fetch
            setEvent(null);
            setLoading(false);
            setError(null);
            return;
        }

        setLoading(true);
        fetchEvent(eventId)
            .then(data => {
                setEvent(data);
                setError(null);
            })
            .catch(err => {
                if (err instanceof Error) setError(err.message);
                else setError("Failed to fetch event");
            })
            .finally(() => setLoading(false));
    }, [eventId]);

    return { event, loading, error };
};

export default useEvent;