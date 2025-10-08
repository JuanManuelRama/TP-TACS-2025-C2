import {useRegistrations} from "$routes/events/[id]/+aux.tsx";
import {kickEvent} from "@/api/events.ts";
import {User} from "$types/user.ts";

const EventRegistrations = ({ eventId } : {eventId:string}) => {
    const { registrations, setRegistrations, loading, error } = useRegistrations(eventId);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    const onRemove = async (userId: string) => {
        try {
            await kickEvent(eventId, userId)
            setRegistrations(prev => {
                return {
                    ...prev,
                    inscriptos: prev.inscriptos.filter(r => r.id !== userId),
                    esperas: prev.esperas.filter(r => r.id !== userId)
                }
        });
        } catch {
            alert("Could not remove user");
        }
    };

    const renderList = (users: User[] = [], title:string)  => (
        <div>
            <h2 className="font-semibold mb-2">{title}</h2>
            <ul className="space-y-2">
                {users.map((r) => (
                    <li key={r.id} className="flex justify-between items-center">
                        <span>{r.username}</span>
                        <button
                            className="text-red-500 text-xs px-2 py-1 border border-red-300 rounded hover:bg-red-50"
                            onClick={() => onRemove(r.id)}
                        >
                            Eliminar
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );

    return <div className="grid grid-cols-2 gap-4">
        {renderList(registrations.inscriptos, "Confirmed")}
        {renderList(registrations.esperas, "Waitlisted")}
    </div>;
};

export default EventRegistrations;