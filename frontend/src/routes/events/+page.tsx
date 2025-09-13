import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "$/src/components/ui/card";
import { Link } from "react-router";
import { Event } from "$types/event"
import useEvents from "$hooks/useEvents.tsx";

const EventCard = ({event}: {event: Event}) => {
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
</Link>)
}


const Page = () => {
    const {events, loading, error} = useEvents();

    if (loading) return <p>Loading events...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="grid gap-4">
            {events.map(event => (
                <EventCard key={event.id} event={event} />
            ))}
        </div>
    );
};



export { Page as Component }; //a
export default Page; //b