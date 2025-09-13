import {useRegistrations} from "$routes/events/[id]/+aux.tsx";
import {kickEvent} from "@/api/events.ts";
import {Registration} from "$types/registration.ts";

const EventRegistrations = ({ eventId } : {eventId:string}) => {
    const { confirmed, setConfirmed, waitlisted, setWaitlisted, loading, error } = useRegistrations(eventId);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    const onRemove = async (userId: string) => {
        try {
            await kickEvent(eventId, userId)
           setConfirmed(prev => prev.filter(r => r.usuario.id !== userId));
           setWaitlisted(prev => prev.filter(r => r.usuario.id !== userId));

        } catch {
            alert("Could not remove user");
        }
    };

    const renderList = (list:Registration[], title:string)  => (
        <div>
            <h2 className="font-semibold mb-2">{title}</h2>
            <ul className="space-y-2">
                {list.map((r) => (
                    <li key={r.usuario.id} className="flex justify-between items-center">
                        <span>{r.usuario.username}</span>
                        <button
                            className="text-red-500 text-xs px-2 py-1 border border-red-300 rounded hover:bg-red-50"
                            onClick={() => onRemove(r.usuario.id)}
                        >
                            Eliminar
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );

    return <div className="grid grid-cols-2 gap-4">{renderList(confirmed, "Confirmed")}{renderList(waitlisted, "Waitlisted")}</div>;
};

export default EventRegistrations;