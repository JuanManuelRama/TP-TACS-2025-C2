import { addEvent } from "$/src/api/events";
import type { NewEvent } from "$/src/types/event";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { AxiosError } from "axios";
import { toast } from "sonner";

export const useAddEventMutation = () => {
	const queryClient = useQueryClient();
	return useMutation({
		mutationFn: (eventValues: NewEvent) => addEvent(eventValues),
		onSuccess: (res, variables) => {
			console.log(res, variables);
			toast.success("Evento agregado exitosamente", {
				description: "El evento " + variables.titulo + " se agrego exitosamente",
			});
			queryClient.invalidateQueries({ queryKey: ["events"] });
		},
		onError: (err: AxiosError, variables) => {
			console.log(err, variables);
		},
	});
};
