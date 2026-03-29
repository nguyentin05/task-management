import { useContext } from "react";
import { MyUserContext } from "../configs/MyContexts";
import { authApis, endpoints } from "../configs/Apis";
import { Button } from "react-bootstrap";

const Home = () => {
  const [user, dispatch] = useContext(MyUserContext);

  const logout = async () => {
    try {
      await authApis().post(endpoints["logout"]);
    } catch (ex) {
      console.error("Logout lỗi:", ex);
    } finally {
      dispatch({ type: "logout" });
    }
  };

  return (
    <>
      <p>Coming soon...</p>
      {user && (
        <Button variant="danger" onClick={logout}>
          Đăng xuất
        </Button>
      )}
    </>
  );
};

export default Home;
