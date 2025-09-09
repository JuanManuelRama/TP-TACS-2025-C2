import { createBrowserRouter, RouterProvider } from 'react-router'

const router = createBrowserRouter([
  {path:'/', element: <div>
    Testing main page
  </div>}
])

function App() {
  // Router Provider.
  // Lingui
  // React query
  // Zod for form
  // Zustand for state manager. (if necesary)


  return <RouterProvider router={router}/>
}

export default App
