import { Outlet } from "react-router"

const Layout = ( )=>{
return <div className="flex flex-1">
    <main>
        <Outlet />
    </main>
</div>
}

export default Layout