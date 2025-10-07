import useSignup from "$/src/hooks/useSignup"
import { Button } from "@/components/ui/button"
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import {
    Field,
    FieldDescription,
    FieldGroup,
    FieldLabel,
} from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Link } from "react-router"
import { formSchema, SignupFormValues } from "./form/schema"

export function Page() {

    const {signup, loading, error} = useSignup();


    const form = useForm<SignupFormValues>({
            resolver: zodResolver(formSchema),
            defaultValues: {
                fullname:"",
                password: "",
                email: ""
            },
        });

    const submit = (data: SignupFormValues) => {
        console.log(data);
        signup(data)    
        
        // Handle form submission here
    };
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
    <Card >
      <CardHeader>
        <CardTitle>Create an account</CardTitle>
        <CardDescription>
          Enter your information below to create your account
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={form.handleSubmit(submit)}>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="name">Full Name</FieldLabel>
              <Input {...form.register("fullname")} id="name" type="text" placeholder="John Doe" required />
              {form.formState.errors.fullname && (
					<p className="text-sm text-red-500">
						{form.formState.errors.fullname.message}
					</p>
				)}
            </Field>
            
            <Field>
              <FieldLabel htmlFor="email">Email</FieldLabel>
              <Input
                id="email"
                type="email"
                placeholder="m@example.com"
                {...form.register("email")}
                required
              />
              <FieldDescription>
                We&apos;ll use this to contact you. We will not share your email
                with anyone else.
              </FieldDescription>
              {form.formState.errors.email && (
					<p className="text-sm text-red-500">
						{form.formState.errors.email.message}
					</p>
				)}
            </Field>
            <Field>
              <FieldLabel htmlFor="password">Password</FieldLabel>
              <Input {...form.register("password")} id="password" type="password" required />
              <FieldDescription>
                Must be at least 8 characters long.
              </FieldDescription>
              {form.formState.errors.password && (
					<p className="text-sm text-red-500">
						{form.formState.errors.password.message}
					</p>
				)}
            </Field>
            <Field>
              <FieldLabel htmlFor="confirm-password">
                Confirm Password
              </FieldLabel>
              <Input {...form.register("confirmationPassword")}  id="confirm-password" type="password" required />
              <FieldDescription>Please confirm your password.</FieldDescription>
              {form.formState.errors.confirmationPassword && (
					<p className="text-sm text-red-500">
						{form.formState.errors.confirmationPassword.message}
					</p>
				)}
            </Field>
            <FieldGroup>
              <Field>
                <Button type="submit">Create Account</Button>
                {/* <Button variant="outline" type="button">
                  Sign up with Google
                </Button> */}
                <FieldDescription className="px-6 text-center">
                  Already have an account? <Link to="/auth/login">
                  <span>Sign in</span>
                  </Link>
                </FieldDescription>
              </Field>
            </FieldGroup>
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
    </div>
    </div>
)
}


export { Page as Component }
export default Page;