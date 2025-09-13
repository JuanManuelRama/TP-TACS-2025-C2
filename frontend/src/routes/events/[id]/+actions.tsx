import {Button} from "$components/ui/button.tsx";
import {deleteEvent, subscribeEvent,} from "@/api/events.ts";
import {useNavigate} from "react-router-dom";

const Actions = ({isOwner, eventId}: {
    isOwner: boolean;
    eventId: string;
}) => {
    const navigate = useNavigate();

    //TODO Change state of button, and make it unsubscribe
    const onSubscribe = async () => {
        try {
            await subscribeEvent(eventId);
        } catch {
            alert("Subscribe event failed");
        }
    }
/*
    const onUnsubscribe = async () => {
        try {
            await unsubscribeEvent(eventId);
            setRegistered(false);
        } catch {
            alert("Unsubscribe event failed");
        }
    }*/

    const onDelete = async () => {
        try {
            await deleteEvent(eventId);
            navigate("/events");
        }
        catch {
            alert("Delete event failed");
        }
    }

    return (
        <div className="flex justify-end mr-10">
            {isOwner ? (
                <Button
                    variant="destructive"
                    onClick={onDelete}>Eliminar</Button>
            ) : <Button onClick={onSubscribe}>Subscribe</Button>
            }
        </div>
    );
};


export default Actions;