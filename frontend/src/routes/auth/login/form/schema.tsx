import { z } from "zod";

export const formSchema = z.object({
	username: z.string().min(1, "Please enter a valid username"),
	password: z.string().min(1, "Password must not be empty"),
});

export type LoginFormValues = z.infer<typeof formSchema>;
