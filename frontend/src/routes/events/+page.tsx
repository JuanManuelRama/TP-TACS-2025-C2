import { Button } from "$/src/components/ui/button";
import { Calendar } from "$/src/components/ui/calendar";
import {
	Card,
	CardDescription,
	CardFooter,
	CardHeader,
	CardTitle,
} from "$/src/components/ui/card";
import { Input } from "$/src/components/ui/input";
import { Popover, PopoverContent, PopoverTrigger } from "$/src/components/ui/popover";
import { cn } from "$/src/lib/utils";
import useEvents from "$hooks/useEvents.tsx";
import type { Event } from "$types/event";
import {
	Empty,
	EmptyContent,
	EmptyDescription,
	EmptyHeader,
	EmptyMedia,
	EmptyTitle,
} from "@/components/ui/empty";
import { format } from "date-fns";
import { Calendar1Icon, CalendarIcon } from "lucide-react";
import React from "react";
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

const ToolBar = () => {
	const [date, setDate] = React.useState<Date | undefined>(
		new Date(2025, 5, 12)
	  )
	return (
		<div className="flex justify-between border p-3 mb-10">
			<div>
				<Input type="text" placeholder="Buscar" />
			</div>
			<Popover>
                <PopoverTrigger asChild>
                  
                    <Button
                      variant={"outline"}
                      className={cn(
                        "w-[240px] pl-3 text-left font-normal",
                        !date && "text-muted-foreground"
                      )}
                    >
                      {date ? (
                        format(date.toDateString() || "", "PPP")
                      ) : (
                        <span>Pick a date</span>
                      )}
                      <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                    </Button>
                  
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                  <Calendar
                    mode="single"
                    selected={date}
                    onSelect={setDate}
                    // disabled={(date) =>
                    //   date > new Date() || date < new Date("1900-01-01")
                    // }
					defaultMonth={date}
					numberOfMonths={2}
                    captionLayout="dropdown"
                  />
                </PopoverContent>
              </Popover>
			  <Link to="/events/new">
			  	<Button>Crear Evento</Button>
			  </Link>
			
		</div>
	);
};

export function EmptyEvent() {
	return (
		<Empty>
			<EmptyHeader>
				<EmptyMedia variant="icon">
					<Calendar1Icon />
				</EmptyMedia>
				<EmptyTitle>No Events Yet</EmptyTitle>
				<EmptyDescription>
					There is no event created yet, try changing the filters of the search
					or go and create your first event 🎉.
				</EmptyDescription>
			</EmptyHeader>
			<EmptyContent>
				<div className="flex gap-2">
					<Link to="/events/new">
						<Button>Create Event</Button>
					</Link>
					{/* <Button variant="outline">Import Project</Button> */}
				</div>
			</EmptyContent>
			<Button
				variant="link"
				asChild
				className="text-muted-foreground"
				size="sm"
			></Button>
		</Empty>
	);
}

const Page = () => {
	const { events, loading, error } = useEvents();

	if (loading) return <p>Loading events...</p>;
	if (error) return <p>Error: {error}</p>;
	if (events.length === 0) return <EmptyEvent />;

	return (
		<>
			<ToolBar />
			<div className="grid gap-4">
				{events.map((event) => (
					<EventCard key={event.id} event={event} />
				))}
			</div>
		</>
	);
};

export { Page as Component }; //a
export default Page; //b
