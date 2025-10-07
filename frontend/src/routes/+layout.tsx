import { Button } from "$components/ui/button.tsx";
import { Bell, CalendarRange, LogOut, UserCircle } from "lucide-react";
import { Link, Outlet, useNavigate } from "react-router";
import { Avatar, AvatarFallback, AvatarImage } from "../components/ui/avatar";
import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "../components/ui/dropdown-menu";
import useAuth from "../hooks/useAuth";
import useBoundStore from "../store";

const AuthButtons = () => {
	const navigate = useNavigate();
	return (
		<div className="flex items-center gap-2">
			<Button variant="outline" onClick={() => navigate("/auth/signup")}>
				Register
			</Button>
			<Button variant="outline" onClick={() => navigate("/auth/login")}>
				Log In
			</Button>
		</div>
	);
};

const UserInfo = () => {

	const {userInformation} = useBoundStore()
	const {logout} = useAuth();
	const navigate = useNavigate();

	return  <DropdownMenu>
	<DropdownMenuTrigger asChild>
	  <Button
		size="lg"
		variant={"outline"}
	  >
		<Avatar className="h-8 w-8 rounded-lg grayscale">
		  <AvatarImage src="" alt={userInformation?.username} />
		  <AvatarFallback className="rounded-lg">{userInformation?.username.slice(0,2).toUpperCase()}</AvatarFallback>
		</Avatar>
		<div className="grid flex-1 text-left text-sm leading-tight">
		  <span className="truncate font-medium">{userInformation?.username}</span>
		  <span className="text-muted-foreground truncate text-xs">
			{userInformation?.username}
		  </span>
		</div>
	  </Button>
	</DropdownMenuTrigger>
	<DropdownMenuContent
	  className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
	  side={"bottom"}
	  align="end"
	  sideOffset={4}
	>
	  <DropdownMenuLabel className="p-0 font-normal">
		<div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
		  <Avatar className="h-8 w-8 rounded-lg">
			<AvatarImage src="" alt={userInformation?.username} />
			<AvatarFallback className="rounded-lg">{
				userInformation?.username.slice(0,2).toUpperCase()
				}</AvatarFallback>
		  </Avatar>
		  <div className="grid flex-1 text-left text-sm leading-tight">
			<span className="truncate font-medium">{userInformation?.username}</span>
			<span className="text-muted-foreground truncate text-xs">
			  {userInformation?.username}
			</span>
		  </div>
		</div>
	  </DropdownMenuLabel>
	  <DropdownMenuSeparator />
	  <DropdownMenuGroup>
		<DropdownMenuItem onClick={() => navigate("/profile")}>
		  <UserCircle />
		  Account
		</DropdownMenuItem>
		<DropdownMenuItem>
		  <CalendarRange />
		  My Events
		</DropdownMenuItem>
		<DropdownMenuItem>
		  <Bell />
		  Notifications
		</DropdownMenuItem>
	  </DropdownMenuGroup>
	  <DropdownMenuSeparator />
	  <DropdownMenuItem onClick={logout}>
		<LogOut />
		Log out
	  </DropdownMenuItem>
	</DropdownMenuContent>
  </DropdownMenu>
}
	
	// <div className="flex items-center gap-2">

	// 	<span>{username}</span>
	// 	<Button variant="outline" onClick={handleLogout}>
	// 		Logout
	// 	</Button>
	// </div>


const Header = () => {
	const {isAuthenticated} = useBoundStore()
	return (
		<header className="h-14 border-b border-gray-200 flex items-center px-5">
			<Link to="/events">
				<span>Event Manager</span>
			</Link>

			<div className="ml-auto flex gap-2">
				{isAuthenticated ? (
					<UserInfo  />
				) : (
					<AuthButtons />
				)}
			</div>
		</header>
	);
};

const Layout = ({ children }: { children?: React.ReactNode }) => {
	return (
		<div className="h-screen">
			<Header />
			<main className="mt-10 mx-40">{children || <Outlet />}</main>
		</div>
	);
};

export default Layout;
