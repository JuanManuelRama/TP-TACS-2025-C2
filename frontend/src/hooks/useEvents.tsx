import { useState, useEffect } from "react";
import {eventList} from "@/api/events.ts";
import {Event} from "@/types/event.ts";

const useEvents = () => {
    const [events, setEvents] = useState<Event[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        eventList()
            .then(data => {
                setEvents(data);
                setError(null);
            })
            .catch(err => {
                setError(err.message || "Failed to fetch events");
            })
            .finally(() => setLoading(false));
    }, []);

    return { events, loading, error };
};

export default useEvents;