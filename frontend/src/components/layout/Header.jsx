import { useContext, useState, useEffect } from "react";
import { Container, Nav, Navbar, Dropdown, Button } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/MyContexts";
import { authApis, endpoints } from "../../configs/Apis";
import cookie from "react-cookies";
import Swal from "sweetalert2";

const Header = () => {
  const [user, dispatch] = useContext(MyUserContext);
  const nav = useNavigate();

  const isAdmin = user?.roles?.some((role) => role.name === "ADMIN");

  const [profile, setProfile] = useState(null);

  useEffect(() => {
    if (user && !isAdmin) {
      authApis()
        .get(endpoints["profiles-me"])
        .then((res) => {
          if (res.data.code === 1000) setProfile(res.data.result);
        })
        .catch((err) => console.error("Không tải được profile ở Header", err));
    }
  }, [user, isAdmin]);

  const logout = async () => {
    const result = await Swal.fire({
      title: "Bạn có chắc chắn muốn đăng xuất không?",
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#28A745",
      cancelButtonColor: "#FF5733",
      confirmButtonText: "Đồng ý",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      try {
        await authApis().post(endpoints["logout"]);
      } catch (ex) {
        console.error("Lỗi logout:", ex);
      } finally {
        cookie.remove("token", { path: "/" });
        cookie.remove("user", { path: "/" });
        dispatch({ type: "logout" });

        await Swal.fire({
          title: "Đăng xuất thành công!",
          text: "Chào tạm biệt.",
          icon: "success",
          timer: 1000,
          showConfirmButton: false,
        });

        nav("/login");
      }
    }
  };

  return (
    <Navbar expand="lg" className="bg-white shadow-sm mb-4 py-3 sticky-top">
      <Container>
        <Navbar.Brand className="d-flex align-items-center" as={Link} to="/">
          <img
            src="/task.png"
            width="32"
            height="32"
            className="d-inline-block align-top me-2"
            alt="Logo"
          />
          <span
            className="fw-bold"
            style={{ color: "#6C757D", fontSize: "1.2rem" }}
          >
            Task Management
          </span>
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto ms-4">
            {user !== null && (
              <Link
                className="nav-link fw-semibold"
                style={{ color: "#6C757D" }}
                to="/w/me"
              >
                <i className="bi bi-grid-1x2 me-1"></i> Bảng công việc
              </Link>
            )}
          </Nav>

          <Nav className="ms-auto d-flex align-items-center">
            {user === null ? (
              <>
                <Link
                  className="nav-link fw-bold px-3"
                  style={{ color: "#6C757D" }}
                  to="/register"
                >
                  Đăng ký
                </Link>
                <Link
                  className="nav-link fw-bold text-white px-3 ms-2 rounded-pill shadow-sm"
                  style={{
                    backgroundColor: "#007BFF",
                    borderColor: "#007BFF",
                  }}
                  to="/login"
                >
                  Đăng nhập
                </Link>
              </>
            ) : isAdmin ? (
              <div className="d-flex align-items-center bg-light rounded-pill px-3 py-1 shadow-sm border">
                <div className="d-flex flex-column text-end me-3">
                  <span
                    className="fw-bold lh-1"
                    style={{ fontSize: "0.9rem", color: "#6C757D" }}
                  >
                    Quản trị viên
                  </span>
                </div>
                <Button
                  size="sm"
                  className="d-flex justify-content-center align-items-center rounded-pill"
                  title="Đăng xuất"
                  style={{
                    backgroundColor: "#FF5733",
                    borderColor: "#FF5733",
                  }}
                  onClick={(e) => {
                    e.preventDefault();
                    logout();
                  }}
                >
                  Đăng xuất
                </Button>
              </div>
            ) : (
              <Dropdown align="end">
                <Dropdown.Toggle
                  variant="light"
                  id="dropdown-custom-components"
                  className="d-flex align-items-center border-0 shadow-sm rounded-pill px-2 py-1 bg-white"
                >
                  <img
                    src={user.profile?.avatar || profile?.avatar}
                    alt="avatar"
                    className="rounded-circle me-2 object-fit-cover"
                    style={{ width: "32px", height: "32px" }}
                  />

                  <span className="fw-bold text-dark me-2 small">
                    {user.profile?.firstName || profile?.firstName}
                  </span>
                </Dropdown.Toggle>

                <Dropdown.Menu className="shadow border-0 mt-2 rounded-3">
                  <div className="px-3 py-2 border-bottom mb-2">
                    <p className="mb-0 fw-bold text-dark">
                      {user.profile?.lastName || profile?.lastName}{" "}
                      {user.profile?.firstName || profile?.firstName}
                    </p>
                    <p
                      className="mb-0 small text-muted text-truncate"
                      style={{ maxWidth: "200px" }}
                    >
                      {user.email}
                    </p>
                  </div>

                  <Dropdown.Item
                    as={Link}
                    to="/me"
                    className="fw-semibold py-2"
                  >
                    <i
                      className="bi bi-person-circle me-2"
                      style={{ color: "#6C757D" }}
                    ></i>{" "}
                    Thông tin cá nhân
                  </Dropdown.Item>

                  <Dropdown.Divider />

                  <Dropdown.Item
                    onClick={(e) => {
                      e.preventDefault();
                      logout();
                    }}
                    className="fw-bold py-2"
                    style={{ color: "#FF5733" }}
                  >
                    <i className="bi bi-box-arrow-right me-2"></i> Đăng xuất
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
