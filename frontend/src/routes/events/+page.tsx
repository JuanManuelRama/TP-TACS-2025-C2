import { Button } from "$/src/components/ui/button";
import {
	Card,
	CardDescription,
	CardFooter,
	CardHeader,
	CardTitle,
} from "$/src/components/ui/card";
import useEvents from "$hooks/useEvents.tsx";
import type { Event } from "$types/event";
import { Link } from "react-router";

const EventCard = ({ event }: { event: Event }) => {
	return (
		<Link to={`/events/${event.id}`}>
			<Card>
				<CardHeader>
					<CardDescription>{event.titulo}</CardDescription>
					<CardTitle className="text-2xl font-semibold tabular-nums @[250px]/card:text-3xl">
						{event.descripcion}
					</CardTitle>
				</CardHeader>
				<CardFooter className="flex-col items-start gap-1.5 text-sm">
					<div className="text-muted-foreground">
						{new Date(event.inicio).toLocaleString()}
					</div>
				</CardFooter>
			</Card>
		</Link>
	);
};

const Page = () => {
	const { events, loading, error } = useEvents();

	if (loading) return <p>Loading events...</p>;
	if (error) return <p>Error: {error}</p>;
	if (events.length === 0)
		return (
			<div className="flex flex-col items-center justify-center">
				<p>No events found</p>
				<Link to="/events/new">
					<Button>Crea tu primer Evento</Button>
				</Link>
			</div>
		);

	return (
		<div className="grid gap-4">
			{events.map((event) => (
				<EventCard key={event.id} event={event} />
			))}
		</div>
	);
};

export { Page as Component }; //a
export default Page; //b
