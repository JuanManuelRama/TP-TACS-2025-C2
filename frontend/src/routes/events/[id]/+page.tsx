import { Button } from "$/src/components/ui/button";
import { useState } from "react";
import {useLocation, useParams} from "react-router";
import {handleRemove, useRegistrations} from "./+aux.tsx";
import useEvent from "@/hooks/useEvent.tsx";

const Actions = ({isOwner}:{isOwner:boolean}) => {
    return (
        <div className="flex justify-end mr-10">
            {isOwner ? (
                <Button variant="destructive">Eliminar</Button>
            ) : (
                <Button>Subscribir</Button>
            )}
        </div>
    );
};

const EventRegistrations = ({eventId, onRemove,}: {
    eventId: string;
    onRemove?: (userId: string) => void;
}) => {
    const { confirmed, setConfirmed, waitlisted, setWaitlisted, loading, error } = useRegistrations(eventId);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="grid grid-cols-2 gap-4">
            <div>
                <h2 className="font-semibold mb-2">Confirmed</h2>
                <ul className="space-y-2">
                    {confirmed.map((r) => (
                        <li
                            key={r.usuario.id}
                            className="flex justify-between items-center"
                        >
                            <span>{r.usuario.username}</span>
                            <button
                                className="text-red-500 text-xs px-2 py-1 border border-red-300 rounded hover:bg-red-50"
                                onClick={() => onRemove?.(eventId, r.usuario.id,
                                    setConfirmed, setWaitlisted)}
                            >
                                Eliminar
                            </button>
                        </li>
                    ))}
                </ul>
            </div>

            <div>
                <h2 className="font-semibold mb-2">Waitlisted</h2>
                <ul className="space-y-2">
                    {waitlisted.map((r) => (
                        <li
                            key={r.usuario.id}
                            className="flex justify-between items-center"
                        >
                            <span>{r.usuario.username}</span>
                            <button
                                className="text-red-500 text-xs px-2 py-1 border border-red-300 rounded hover:bg-red-50"
                                onClick={() => onRemove?.(eventId, r.usuario.id,
                                    setConfirmed, setWaitlisted)}
                            >
                                Eliminar
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

const Page = () => {
    const location = useLocation();
    const params = useParams();
    const stateEvent = location.state?.event;
    const { event: fetchedEvent, loading, error } = useEvent(params.id ?? null);
    const event = stateEvent || fetchedEvent;
    const [ownerView, setOwnerView] = useState<boolean>(false) // temporarily for testing, in final product you'll be locked to the correct one
    if (!params.id) return <div>404</div>;
    if (!event && loading) return <p>Loading event...</p>;
    if (!event && error) return <p>Error: {error}</p>;
    return (
        <div className="space-y-6">
            {/* Owner/guest toggle */}
            <div className="flex gap-2">
                <Button variant="outline" onClick={() => setOwnerView(true)}>
                    Owner View
                </Button>
                <Button variant="outline" onClick={() => setOwnerView(false)}>
                    Guest View
                </Button>
            </div>

            {/* Description of what owner can do */}
            <div className="text-sm text-gray-700">
                {ownerView ? (
                    <>
                    <p>With <strong>owner view</strong>: </p>
                        <ul className="list-disc ml-6">
                            <li>I can delete the event.</li>
                            <li>I can remove people from subscription.</li>
                            <li>I can see who is listed.</li>
                        </ul>
                    </>

                ) : (
                    <p>With <strong>guest view</strong>: I can only see event details and subscribe.</p>
                )}
            </div>

            {/* Event info */}
            <div className="border rounded-xl p-6 shadow bg-white">
                <h1 className="text-3xl font-bold mb-2">{event.titulo}</h1>
                <p className="text-gray-600 mb-4">{event.descripcion}</p>

                <div className="grid grid-cols-2 gap-4 text-sm">
                    <p>
                        <span className="font-semibold">Organizador:</span> {event.organizador.username}
                    </p>

                    <p>
                        <span className="font-semibold">Inicio:</span>{" "}
                        {new Date(event.inicio).toLocaleString()}
                    </p>
                    <p>
                        <span className="font-semibold">Duración:</span> {event.duracion} min
                    </p>
                    <p>
                        <span className="font-semibold">Cupo mínimo:</span>{" "}
                        {event.cupoMinimo ?? "N/A"}
                    </p>
                    <p>
                        <span className="font-semibold">Cupo máximo:</span> {event.cupoMaximo}
                    </p>
                    <p>
                        <span className="font-semibold">Precio:</span> ${event.precio}
                    </p>
                    <p>
                        <span className="font-semibold">Categorías:</span>{" "}
                        {event.categorias.join(", ")}
                    </p>
                </div>
            </div>

            <Actions isOwner={ownerView}></Actions>
            {/* Event registrations (only for owner) */}
            {ownerView && <EventRegistrations eventId={params.id} onRemove={handleRemove}/>}
        </div>
    );
}




export { Page as Component };
export default Page;
