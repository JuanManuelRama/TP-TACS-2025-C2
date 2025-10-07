import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
	Form,
	FormControl,
	FormDescription,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
	Popover,
	PopoverContent,
	PopoverTrigger,
} from "@/components/ui/popover";
import { cn } from "@/lib/utils";
import { zodResolver } from "@hookform/resolvers/zod";
import { format } from "date-fns";
import { CalendarIcon } from "lucide-react";
import { useForm } from "react-hook-form";
import type { z } from "zod";
import formSchema, { type EventFormValues } from "./schema";

export function EventForm({
	onSubmit,
}: {
	onSubmit: (values: EventFormValues) => void;
}) {
	const form = useForm<EventFormValues>({
		resolver: zodResolver(formSchema),
		defaultValues: {
			titulo: "",
			descripcion: "",
			cupoMaximo: 0,
			cupoMinimo: 0,
			precio: 0,
			categorias: [],
			inicio: new Date(),
			duracion: 0,
		},
	});

	// 2. Define a submit handler.
	function onSubmitForm(values: z.infer<typeof formSchema>) {
		// Do something with the form values.
		// ✅ This will be type-safe and validated.
		onSubmit(values);
	}
	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(onSubmitForm)} className="space-y-6">
				{/* Titulo */}
				<FormField
					control={form.control}
					name="titulo"
					render={({ field }) => (
						<FormItem>
							<FormLabel>Titulo</FormLabel>
							<FormControl>
								<Input placeholder="Ej: Concierto de rock" {...field} />
							</FormControl>
							<FormMessage />
						</FormItem>
					)}
				/>

				{/* Descripcion */}
				<FormField
					control={form.control}
					name="descripcion"
					render={({ field }) => (
						<FormItem>
							<FormLabel>Descripción</FormLabel>
							<FormControl>
								<textarea
									placeholder="Describe el evento"
									className={cn(
										"flex min-h-24 w-full rounded-md border border-input bg-background px-3 py-2 text-sm",
										"ring-offset-background placeholder:text-muted-foreground",
										"focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2",
										"disabled:cursor-not-allowed disabled:opacity-50",
									)}
									{...field}
								/>
							</FormControl>
							<FormMessage />
						</FormItem>
					)}
				/>

				{/* Cupo minimo y maximo */}
				<div className="grid grid-cols-1 gap-4 md:grid-cols-2">
					<FormField
						control={form.control}
						name="cupoMinimo"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Cupo mínimo</FormLabel>
								<FormControl>
									<Input
										type="number"
										min={0}
										{...field}
										onChange={(e) => field.onChange(e.target.valueAsNumber)}
									/>
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
					<FormField
						control={form.control}
						name="cupoMaximo"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Cupo máximo</FormLabel>
								<FormControl>
									<Input
										type="number"
										min={0}
										{...field}
										onChange={(e) => field.onChange(e.target.valueAsNumber)}
									/>
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
				</div>

				{/* Precio y Duración */}
				<div className="grid grid-cols-1 gap-4 md:grid-cols-2">
					<FormField
						control={form.control}
						name="precio"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Precio</FormLabel>
								<FormControl>
									<Input
										type="number"
										min={0}
										step="0.01"
										{...field}
										onChange={(e) => field.onChange(e.target.valueAsNumber)}
									/>
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
					<FormField
						control={form.control}
						name="duracion"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Duración (minutos)</FormLabel>
								<FormControl>
									<Input
										type="number"
										min={0}
										{...field}
										onChange={(e) => field.onChange(e.target.valueAsNumber)}
									/>
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
				</div>

				{/* Categorias */}
				<FormField
					control={form.control}
					name="categorias"
					render={({ field }) => (
						<FormItem>
							<FormLabel>Categorías</FormLabel>
							<FormDescription>
								Separá por comas. Ej: musica, deportes, tecnología
							</FormDescription>
							<FormControl>
								<Input
									placeholder="musica, deportes"
									value={(field.value ?? []).join(", ")}
									onChange={(e) => {
										const list = e.target.value
											.split(/,/) // split by comma
											.map((s) => s.trim())
											.filter(Boolean);
										field.onChange(list);
									}}
								/>
							</FormControl>
							<FormMessage />
						</FormItem>
					)}
				/>

				{/* Fecha de inicio */}
				<FormField
					control={form.control}
					name="inicio"
					render={({ field }) => (
						<FormItem className="flex flex-col">
							<FormLabel>Fecha de inicio</FormLabel>
							<Popover>
								<PopoverTrigger asChild>
									<FormControl>
										<Button
											variant={"outline"}
											className={cn(
												"w-[260px] justify-start text-left font-normal",
												!field.value && "text-muted-foreground",
											)}
										>
											<CalendarIcon className="mr-2 h-4 w-4" />
											{field.value ? (
												format(field.value, "PPP")
											) : (
												<span>Elegí una fecha</span>
											)}
										</Button>
									</FormControl>
								</PopoverTrigger>
								<PopoverContent className="w-auto p-0" align="start">
									<Calendar
										mode="single"
										selected={field.value}
										onSelect={(d) => field.onChange(d)}
										defaultMonth={field.value}
										numberOfMonths={2}
										captionLayout="dropdown"
									/>
								</PopoverContent>
							</Popover>
							<FormMessage />
						</FormItem>
					)}
				/>

				<div className="pt-2">
					<Button type="submit">Crear evento</Button>
				</div>
			</form>
		</Form>
	);
}
