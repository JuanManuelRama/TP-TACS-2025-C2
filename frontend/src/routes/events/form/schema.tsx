import { z } from "zod";

const formSchema = z.object({
	titulo: z.string().min(2, {
		message: "Titulo must be at least 2 characters.",
	}),
	descripcion: z.string().min(2, {
		message: "Descripcion must be at least 2 characters.",
	}),
	cupoMaximo: z.number().min(1, {
		message: "Cupo maximo must be at least 1.",
	}),
	cupoMinimo: z.number().min(1, {
		message: "Cupo minimo must be at least 1.",
	}),
	precio: z.number(),
	categorias: z.array(z.string()),
	inicio: z.date(),
	duracion: z.number(),
});

export default formSchema;

export type EventFormValues = z.infer<typeof formSchema>;
