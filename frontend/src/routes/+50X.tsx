/* -------------------------------------------- 50X.tsx  -- */

import { useRouteError } from "react-router";

import Layout from "./+layout";

type RouteError = {
	message?: string;
};

/* ------------------------------------------- component -- */

export function Page() {
	const error = useRouteError() as RouteError;

	return (
		<Layout>
			<div className="flex flex-col gap-4">
				<div>50X</div>
				<div>
					<pre>{JSON.stringify(error, null, 4)}</pre>
				</div>
			</div>
		</Layout>
	);
}

export { Page as Component };
export default Page;

/* --------------------------------------------- comments -- ;
/* -------------------------------------------------------- */
