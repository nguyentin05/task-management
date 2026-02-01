import { useContext } from "react";
import { Button, Container, Nav, Navbar } from "react-bootstrap";
import { Link } from "react-router-dom";
import { MyUserContext } from "../../configs/MyContexts";

const Header = () => {
  const [user, dispatch] = useContext(MyUserContext);

  return (
    <>
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
                  <Button
                    variant="danger"
                    onClick={() => dispatch({ type: "logout" })}
                  >
                    Đăng xuất
                  </Button>
                </>
              )}
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </>
  );
};

export default Header;
