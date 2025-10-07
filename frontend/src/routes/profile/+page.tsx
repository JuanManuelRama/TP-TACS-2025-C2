import useBoundStore from "$/src/store";

const Page = () => {
	const { userInformation } = useBoundStore();
	return <div>Perfil de {userInformation?.username}</div>;
};
export { Page as Component };
export default Page;
