import AppleButton from "$/src/components/custom/appleButton";
import GoogleButton from "$/src/components/custom/googleButton";
import MetaButton from "$/src/components/custom/metaButton";
import { Card, CardContent } from "$/src/components/ui/card";
import useAuth from "$/src/hooks/useAuth";
import EventImage from "@/assets/pexels-bertellifotografia-2608517.jpg";
import { Link } from "react-router";
import { LoginForm } from "./form/LoginForm";
import type { LoginFormValues } from "./form/schema";

const Page = () => {
	const { login } = useAuth();

	// error => if we get an error.
	const onSubmit = (payload: LoginFormValues) => {
		login(payload.username, payload.password);
	};

	return (
		<div className="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
			<div className="w-full max-w-sm md:max-w-3xl">
				<div className="flex flex-col gap-6">
					<Card className="overflow-hidden p-0">
						<CardContent className="grid p-0 md:grid-cols-2">
							<div className="p-6 md:p-8">
								<div className="flex flex-col gap-6">
									<div className="flex flex-col items-center text-center">
										<h1 className="text-2xl font-bold">Welcome back</h1>
										<p className="text-muted-foreground text-balance">
											Login to your Event Manager account
										</p>
									</div>
									<LoginForm onSubmission={onSubmit} />
									{import.meta.env.VITE_EXTERNAL_PROVIDERS === "true" && (
										<>
											<div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
												<span className="bg-card text-muted-foreground relative z-10 px-2">
													Or continue with
												</span>
											</div>
											<div className="grid grid-cols-3 gap-4">
												<AppleButton />
												<GoogleButton />
												<MetaButton />
											</div>
										</>
									)}

									<div className="text-center text-sm">
										Don&apos;t have an account?{" "}
										<Link to="/auth/signup">
											<span className="underline underline-offset-4">
												Sign up
											</span>
										</Link>
									</div>
								</div>
							</div>
							<div className="bg-muted relative hidden md:block">
								<img
									src={EventImage}
									alt="Event Logo"
									className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
								/>
							</div>
						</CardContent>
					</Card>
					<div className="text-muted-foreground *:[a]:hover:text-primary text-center text-xs text-balance *:[a]:underline *:[a]:underline-offset-4">
						By clicking continue, you agree to our{" "}
						<Link to="/terms">
							<span className="underline underline-offset-4">
								Terms of Service
							</span>
						</Link>{" "}
						and{" "}
						<Link to="/policy">
							<span className="underline underline-offset-4">
								Privacy Policy
							</span>
						</Link>
						.
					</div>
				</div>
			</div>
		</div>
	);
};

export { Page as Component };
export default Page;
