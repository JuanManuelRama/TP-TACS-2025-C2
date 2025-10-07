import { useEventInformation } from "$/src/hooks/api/events/useEventInformation.tsx";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { useParams } from "react-router";
import Actions from "./+actions.tsx";
import EventRegistrations from "./+registration.tsx";

const Page = () => {
	const params = useParams();
	const {
		data: event,
		isLoading,
		error,
	} = useEventInformation(params.id ?? "");

	// Throw an error if there is not a event Id

	const [ownerView, setOwnerView] = useState<boolean>(false); // temporarily for testing, in final product you'll be locked to the correct one
	if (!params.id) return <div>404</div>;
	if (isLoading) return <p>Loading event...</p>;
	if (error) return <p>Error: {error.message}</p>;
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
						<p>
							With <strong>owner view</strong>:{" "}
						</p>
						<ul className="list-disc ml-6">
							<li>I can delete the event.</li>
							<li>I can remove people from subscription.</li>
							<li>I can see who is listed.</li>
						</ul>
					</>
				) : (
					<p>
						With <strong>guest view</strong>: I can only see event details and
						subscribe.
					</p>
				)}
			</div>

			{/* Event info */}
			<div className="border rounded-xl p-6 shadow bg-white">
				<h1 className="text-3xl font-bold mb-2">{event.titulo}</h1>
				<p className="text-gray-600 mb-4">{event.descripcion}</p>

				<div className="grid grid-cols-2 gap-4 text-sm">
					<p>
						<span className="font-semibold">Organizador:</span>{" "}
						{event.organizador.username}
					</p>

					<p>
						<span className="font-semibold">Inicio:</span>{" "}
						{new Date(event.inicio).toLocaleString()}
					</p>
					<p>
						<span className="font-semibold">Duración:</span> {event.duracion}{" "}
						min
					</p>
					<p>
						<span className="font-semibold">Cupo mínimo:</span>{" "}
						{event.cupoMinimo ?? "N/A"}
					</p>
					<p>
						<span className="font-semibold">Cupo máximo:</span>{" "}
						{event.cupoMaximo}
					</p>
					<p>
						<span className="font-semibold">Precio:</span> ${event.precio}
					</p>
					{/* <p>
						<span className="font-semibold">Categorías:</span>{" "}
						{event.categorias.length > 0 && event.categorias.join(" + ")}
					</p> */}
				</div>
			</div>

			<Actions isOwner={ownerView} event={event}></Actions>
			{/* Event registrations (only for owner) */}
			{ownerView && <EventRegistrations eventId={event.id} />}
		</div>
	);
};

export { Page as Component };
export default Page;
