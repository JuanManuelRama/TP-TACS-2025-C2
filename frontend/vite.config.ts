import strip from "@rollup/plugin-strip"; //f
import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react";
import path from "path";
import { defineConfig } from "vite";
import tsConfigPath from "vite-tsconfig-paths"; //d

// https://vite.dev/config/
export default defineConfig({
	base: "/",
	plugins: [
		{
			...strip({
				include: ["**/*.js", "**/*.ts", "**/*.vue", "**/*.jsx", "**/*.tsx"],
				functions: ["console.*", "assert.*", "debug", "alert", "dbg"],
			}),
			apply: "build",
		},
    tsConfigPath({
			loose: true,
		}),
		react(),
		tailwindcss(),
	],

	resolve: {
		alias: {
			"@": path.resolve(__dirname, "./src"),
		},
	},
});
