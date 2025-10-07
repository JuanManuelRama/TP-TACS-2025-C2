import { event } from "$/src/api/events"
import { useQuery } from "@tanstack/react-query"

export const useEventInformation = (eventId:string) => {
    return useQuery({
        queryKey: ["events", eventId],
        queryFn: () => event(eventId)
    })
}


// Extended EventInformation for Owner View.
// Join this information with the users that have been register.