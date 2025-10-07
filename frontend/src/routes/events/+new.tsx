import { Button } from "$/src/components/ui/button";
import { useAddEventMutation } from "$/src/hooks/api/events/addEventMutation";
import type { NewEvent } from "$/src/types/event";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft } from "lucide-react";
import { useNavigate } from "react-router";
import { EventForm } from "./form/EventForm";
import type { EventFormValues } from "./form/schema";

const Page = () => {
	const { mutate } = useAddEventMutation();
    const navigate = useNavigate();
	const onSubmit = (data: EventFormValues) => {
		mutate(data as NewEvent,{
            onSuccess: (response) => {
                navigate(`/events/${response.id}`);
            }
        });
	};
	return (
		<div className="mx-auto max-w-2xl p-4">
			<div className="mb-5">
				<Button variant={"outline"} onClick={() => navigate("/events")}>
					<ArrowLeft />
					Back
				</Button>
			</div>

			<Card>
				<CardHeader>
					<CardTitle>Crear nuevo evento</CardTitle>
				</CardHeader>
				<CardContent>
					<EventForm onSubmit={onSubmit} />
				</CardContent>
			</Card>
		</div>
	);
};

export { Page as Component }; //a
export default Page; //b
