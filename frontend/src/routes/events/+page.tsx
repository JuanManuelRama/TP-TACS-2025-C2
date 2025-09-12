import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "$/src/components/ui/card";
import { Link } from "react-router";
import {useEffect, useState} from "react";
import {Event} from "$types/event"

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
    const [events, setEvents] = useState<Event[]>([]);

    useEffect(() => {
        fetch("http://localhost:8080/eventos")
            .then(res => res.json())
            .then(data => setEvents(data));
    }, []);

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