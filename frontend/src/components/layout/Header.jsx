import { useContext } from "react";
import { Button, Container, Nav, Navbar } from "react-bootstrap";
import { Link } from "react-router-dom";
import { MyUserContext } from "../../configs/MyContexts";
import { authApis, endpoints } from "../../configs/Apis";

const Header = () => {
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
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Task Management Website</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav>
            <Link className="nav-link" to="/">
              Trang chủ
            </Link>

            {user === null ? (
              <>
                <Link className="nav-link text-success" to="/register">
                  Đăng ký
                </Link>
                <Link className="nav-link text-primary" to="/login">
                  Đăng nhập
                </Link>
              </>
            ) : (
              <>
                <Link className="nav-link text-primary" to="/profiles/me">
                  Thông tin của bạn
                </Link>
                <Button variant="danger" onClick={logout}>
                  Đăng xuất
                </Button>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;