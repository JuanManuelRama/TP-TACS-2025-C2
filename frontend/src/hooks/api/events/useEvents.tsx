import { eventList } from "$/src/api/events"
import { useQuery } from "@tanstack/react-query"

export const useEventList = () => {
    return useQuery({
        queryKey: ["events"],
        queryFn: () => eventList()
    })
}