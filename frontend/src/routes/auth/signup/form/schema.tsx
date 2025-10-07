import { z } from "zod";

export const formSchema = z
	.object({
		password: z.string().min(1, "Password must not be empty"),
		fullname: z.string().min(1, "Name is required"),
		email: z.string().min(1, "Email is required"),
		confirmationPassword: z
			.string()
			.min(1, "Confirmation password is required"),
	})
	.superRefine(({ password, confirmationPassword }, ctx) => {
		if (confirmationPassword !== password) {
			ctx.addIssue({
				code: z.ZodIssueCode.custom,
				message: "Passwords must match",
				path: ["confirmationPassword"],
			});
		}
	});

export type SignupFormValues = z.infer<typeof formSchema>;
