import useBoundStore from "$/src/store";
import { Button } from "$components/ui/button.tsx";
import { useSubscription } from "$routes/events/[id]/+aux.tsx";
import { deleteEvent, subscribeEvent, unsubscribeEvent } from "@/api/events.ts";
import type { Event } from "@/types/event.ts";
import { useState } from "react";
import { useNavigate } from "react-router";

const Actions = ({ isOwner, event }: { isOwner: boolean; event: Event }) => {
	const navigate = useNavigate();
	const { isSubscribed, setSubscribed } = useSubscription(event);
	const [message, setMessage] = useState<string | null>(null);
	const {userInformation} = useBoundStore()

	const onSubscribe = async () => {
		try {
			if (!userInformation) {
				navigate("/auth/login");
				return;
			}
			const result = await subscribeEvent(event.id);
			setSubscribed(true);
			if (result.tipo == "CONFIRMACION")
				setMessage("You have been confirmed for this event");
			else
				setMessage(
					"Unfortunately, there were no more spots left, you have been waitlisted",
				);
		} catch {
			alert("Subscribe event failed");
		}
	};

	const onUnsubscribe = async () => {
		try {
			await unsubscribeEvent(event.id);
			setMessage("You have been unsubscribed for this event");
			setSubscribed(false);
		} catch {
			alert("Unsubscribe event failed");
		}
	};

	const onDelete = async () => {
		try {
			await deleteEvent(event.id);
			navigate("/events");
		} catch {
			alert("Delete event failed");
		}
	};

	return (
		<div className="flex flex-col items-end mr-10">
			<div className="flex gap-2">
				{isOwner ? (
					<Button variant="destructive" onClick={onDelete}>
						Eliminar
					</Button>
				) : isSubscribed ? (
					<Button onClick={onUnsubscribe}>Unsubscribe</Button>
				) : (
					<Button onClick={onSubscribe}>Subscribe</Button>
				)}
			</div>

			{message && (
				<div
					className="mt-2 px-3 py-1 rounded-md text-sm font-medium shadow-sm
                    bg-gray-100 text-gray-800"
				>
					{message}
				</div>
			)}
		</div>
	);
};

export default Actions;
