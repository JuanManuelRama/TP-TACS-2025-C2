import { MutationCache, QueryCache, QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import './index.css'


export const queryClient = new QueryClient({
	// Its better to use queryCache and mutationCache because you avoid multiple errors
	queryCache: new QueryCache({
		onError: (err) => {
			// eslint-disable-next-line no-console
			console.error(err);
		},
	}),
	mutationCache: new MutationCache({
		onError: (err) => {
			// eslint-disable-next-line no-console
			console.error(err);
		},
	}),
	defaultOptions: {
		queries: {
			retry: false,
			refetchOnWindowFocus: false,
			// 2min stale time default.
			staleTime: 1000 * 60 * 2,
		},
	},
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
    <App />
    </QueryClientProvider>
  </StrictMode>,
)
