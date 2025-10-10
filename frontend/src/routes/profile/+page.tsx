import useBoundStore from "$/src/store";
import type { UserEvents } from "@/types/user.ts";
import { getEvents } from "@/api/users.ts";
import { useState, useEffect } from "react";
import {Link} from "react-router";

const Page = () => {
    const { userInformation } = useBoundStore();
    const [eventos, setEventos] = useState<UserEvents | null>(null);

    useEffect(() => {
        getEvents().then((data) => setEventos(data));
    }, []);

    if (!eventos) return <div>Loading events...</div>;

    const renderEvents = (title: string, list?: typeof eventos.eventosCreados) => (
        <div className="mb-6">
            <h2 className="text-lg font-semibold mb-2">{title}</h2>
            {list && list.length > 0 ? (
                <ul className="space-y-1">
                    {list.map((e) => (
                        <li
                            key={e.id}
                            className="p-2 border rounded-md hover:bg-gray-50 transition-colors"
                        >
                            <Link to={`/events/${e.id}`}>
                            {e.titulo}
                            </Link>
                        </li>
                    ))}
                </ul>
            ) : (
                <p className="text-gray-500 italic">No hay eventos</p>
            )}
        </div>
    );

    return (
        <div className="max-w-3xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-6">
                Perfil de {userInformation?.username}
            </h1>

            {renderEvents("Eventos Creados", eventos.eventosCreados)}
            {renderEvents("Eventos En Espera", eventos.eventosEnEspera)}
            {renderEvents("Eventos Confirmados", eventos.eventosConfirmados)}
        </div>
    );
};
export { Page as Component };
export default Page;
