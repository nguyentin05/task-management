import { useContext } from "react";
import { Container, Nav, Navbar, Dropdown } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/MyContexts";
import { authApis, endpoints } from "../../configs/Apis";
import cookie from "react-cookies";
import Swal from "sweetalert2";

const Header = () => {
  const [user, dispatch] = useContext(MyUserContext);
  const nav = useNavigate();

  const isAdmin = user?.roles?.some((role) => role.name === "ADMIN");

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
        // ✅ Dùng authApis() để gửi kèm token
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
          timer: 1500,
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
          <span className="fw-bold" style={{ color: "#2C3E50", fontSize: "1.2rem" }}>
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
                  className="nav-link fw-bold px-3 ms-2 text-white bg-primary rounded-pill shadow-sm"
                  to="/login"
                >
                  Đăng nhập
                </Link>
              </>
            ) : (
              <Dropdown align="end">
                <Dropdown.Toggle
                  variant="light"
                  id="dropdown-custom-components"
                  className="d-flex align-items-center border-0 shadow-sm rounded-pill px-2 py-1 bg-white"
                >
                  {user.profile?.avatar ? (
                    <img
                      src={user.profile.avatar}
                      alt="avatar"
                      className="rounded-circle me-2 object-fit-cover"
                      style={{ width: "32px", height: "32px" }}
                    />
                  ) : (
                    <div
                      className="bg-primary text-white rounded-circle d-flex justify-content-center align-items-center me-2"
                      style={{ width: "32px", height: "32px", fontSize: "0.9rem" }}
                    >
                      {user.profile?.firstName?.charAt(0) || user.email?.charAt(0).toUpperCase()}
                    </div>
                  )}
                  <span className="fw-bold text-dark me-2 small">
                    {user.profile?.firstName || user.email?.split("@")[0]}
                  </span>
                </Dropdown.Toggle>

                <Dropdown.Menu className="shadow border-0 mt-2 rounded-3">
                  <div className="px-3 py-2 border-bottom mb-2">
                    <p className="mb-0 fw-bold text-dark">
                      {user.profile?.lastName} {user.profile?.firstName}
                    </p>
                    <p className="mb-0 small text-muted text-truncate" style={{ maxWidth: "200px" }}>
                      {user.email}
                    </p>
                  </div>

                  <Dropdown.Item as={Link} to="/me" className="fw-semibold text-secondary py-2">
                    <i className="bi bi-person-circle me-2"></i> Hồ sơ cá nhân
                  </Dropdown.Item>

                  {isAdmin && (
                    <Dropdown.Item as={Link} to="/admin" className="fw-semibold text-primary py-2">
                      <i className="bi bi-shield-lock-fill me-2"></i> Quản trị hệ thống
                    </Dropdown.Item>
                  )}

                  <Dropdown.Divider />

                  <Dropdown.Item
                    onClick={(e) => { e.preventDefault(); logout(); }}
                    className="fw-bold text-danger py-2"
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