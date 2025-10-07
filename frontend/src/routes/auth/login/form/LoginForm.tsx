import { Spinner } from "$/src/components/ui/spinner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { formSchema, type LoginFormValues } from "./schema";

export function LoginForm({
	onSubmission,
	loading
}: {
	onSubmission: (data: LoginFormValues) => void;
	loading: boolean
}) {
	const form = useForm<LoginFormValues>({
		resolver: zodResolver(formSchema),
		defaultValues: {
			username: "",
			password: "",
		},
	});

	const submit = (data: LoginFormValues) => {
		onSubmission(data);
		// Handle form submission here
	};

	return (
		<form onSubmit={form.handleSubmit(submit)} className="flex flex-col gap-6">
			<div className="grid gap-3">
				<Label htmlFor="username">Username</Label>
				<Input
					type="text"
					placeholder="m@example.com"
					{...form.register("username")}
				/>
				{form.formState.errors.username && (
					<p className="text-sm text-red-500">
						{form.formState.errors.username.message}
					</p>
				)}
			</div>
			<div className="grid gap-3">
				<div className="flex items-center">
					<Label htmlFor="password">Password</Label>
					<button
						type="button"
						className="ml-auto text-sm underline-offset-2 hover:underline"
					>
						Forgot your password?
					</button>
				</div>
				<Input type="password" {...form.register("password")} />
				{form.formState.errors.password && (
					<p className="text-sm text-red-500">
						{form.formState.errors.password.message}
					</p>
				)}
			</div>
			<Button type="submit" className="w-full" disabled={loading}>
				{loading && <Spinner />}
				Login
			</Button>
		</form>
	);
}
