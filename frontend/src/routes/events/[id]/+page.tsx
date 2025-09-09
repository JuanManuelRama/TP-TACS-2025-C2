import { Button } from "$/src/components/ui/button";
import { useState } from "react";
import { useParams } from "react-router";

const mockedUsers = [{
    id:"1",
    name:"Jorge",
    lastname:"Luis",
    email:"dindare@gmail.com",
}]


const Actions = ({isOwner}:{isOwner:boolean}) => {
    return (
<div className=" flex justify-end mr-10">
{isOwner ? <div>
            <Button>Eliminar</Button>
        </div> : <div><Button>Subscribirse</Button></div>}
</div>
        

    ) 
}

const Page = () => {
 const params = useParams()
 const [ownerView, setOwnerView] = useState<boolean>(true)
 if(!params.id){
    return <div>404</div>
 }
 return (
    <div>
        
        <Actions isOwner={ownerView}/>
        
        <p>
            Event: {params.id}
        </p>

        <div className="flex gap-2">
        <Button variant={"outline"} onClick={() => setOwnerView(true)}>
            Owner View
        </Button>
        <Button variant={"outline"} onClick={() => setOwnerView(false)}>
            Guest View
        </Button>
        </div>
        
        <p>
            With owner view:
            - I can delete the event.
            - I can remove people from subscription.

        </p>
        <div className="grid grid-cols-2 gap-2">
            <div>
            <h2>
            Inscriptos
        </h2>
        <ul>
            {mockedUsers.map((user) => (
                <li className="border px-2" key={user.id}>
                    {user.name} {user.lastname}
                </li>
            ))}
        </ul>
        
            </div>
            <div>
            <h2>
            Lista Pendiente
        </h2>
        <ul>
            {mockedUsers.map((user) => (
                <li className="border px-2" key={user.id}>
                    {user.name} {user.lastname}
                </li>
            ))}
        </ul>
            </div>
        </div>
        
        
    </div>
 )
}

export { Page as Component };
export default Page;
