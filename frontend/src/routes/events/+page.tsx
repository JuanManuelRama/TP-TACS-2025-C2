import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "$/src/components/ui/card";
import { Link } from "react-router";

interface Event {
    id: string;
    name: string;
    description: string;
    date: string;
    location: string;
    price: number;
    image: string;
}

const EventCard = ({event}: {event: Event}) => {
    return (
    <Link to={`/events/${event.id}`}>
    <Card>
    <CardHeader>
      <CardDescription>{event.name}</CardDescription>
      <CardTitle className="text-2xl font-semibold tabular-nums @[250px]/card:text-3xl">
        {event.description}
      </CardTitle>
      
    </CardHeader>
    <CardFooter className="flex-col items-start gap-1.5 text-sm">
      
      <div className="text-muted-foreground">
        {event.date}
      </div>
    </CardFooter>
  </Card>
</Link>)
}


const Page = () => {
    return (
        <div>
            <EventCard event={{
                id: "1",
                name: "Event 1",
                description: "Description 1",
                date: "2025-09-09",
                location: "Location 1",
                price: 100,
                image: "https://via.placeholder.com/150"
            }}/>
        </div>
    )
}

export { Page as Component }; //a
export default Page; //b