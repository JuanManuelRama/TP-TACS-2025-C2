import { useParams } from "react-router";

const Page = () => {
 const params = useParams()
 if(!params.id){
    return <div>404</div>
 }
 return (
    <div>
        Event: {params.id}
    </div>
 )
}

export { Page as Component };
export default Page;
