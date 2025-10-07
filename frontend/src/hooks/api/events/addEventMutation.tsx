import { addEvent } from "$/src/api/events";
import type { NewEvent } from "$/src/types/event";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { AxiosError } from "axios";

export const useAddEventMutation = () => {
	const queryClient = useQueryClient();
	return useMutation({
		mutationFn: (eventValues: NewEvent) => addEvent(eventValues),
		onSuccess: (res, variables) => {
			console.log(res, variables);
			queryClient.invalidateQueries({ queryKey: ["events"] });
		},
		onError: (err: AxiosError, variables) => {
			console.log(err, variables);
		},
	});
};
