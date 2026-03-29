import { useEffect, useState, useRef } from "react";
import {
  Container,
  Card,
  Button,
  Form,
  Table,
  Modal,
  Badge,
  Row,
  Col,
  Spinner,
} from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import Swal from "sweetalert2";

const Users = () => {
  const [loading, setLoading] = useState(true);
  const loadingStartTime = useRef(null);
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [newUser, setNewUser] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    roles: [],
  });
  const [showEdit, setShowEdit] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [editProfile, setEditProfile] = useState({
    firstName: "",
    lastName: "",
    dob: "",
    phoneNumber: "",
  });
  const [editRoles, setEditRoles] = useState([]);
  const [avatarFile, setAvatarFile] = useState(null);
  const [showReset, setShowReset] = useState(false);
  const [resetData, setResetData] = useState({ userId: null, newPassword: "" });

  const ensureSpinnerMinTime = () => {
    if (!loadingStartTime.current) return Promise.resolve();
    const displayTime = Date.now() - loadingStartTime.current;
    if (displayTime < 500)
      return new Promise((r) => setTimeout(r, 500 - displayTime));
    return Promise.resolve();
  };

  const loadData = async () => {
    setLoading(true);
    loadingStartTime.current = Date.now();
    try {
      const api = authApis();
      const [usersRes, profilesRes, rolesRes] = await Promise.all([
        api.get(`${endpoints["get-all-users"]}?size=100`),
        api.get(`${endpoints["get-all-profiles"]}?size=100`),
        api.get(`${endpoints["get-all-roles"]}?size=50`),
      ]);

      await ensureSpinnerMinTime();

      if (rolesRes.data.code === 1000) {
        const roleData = rolesRes.data.result.data || rolesRes.data.result;
        setRoles(Array.isArray(roleData) ? roleData : []);
      }

      if (usersRes.data.code === 1000 && profilesRes.data.code === 1000) {
        const uData = usersRes.data.result.data || usersRes.data.result;
        const pData = profilesRes.data.result.data || profilesRes.data.result;

        const combinedUsers = (Array.isArray(uData) ? uData : []).map(
          (user) => {
            const profile =
              (Array.isArray(pData) ? pData : []).find(
                (p) => p.userId === user.id,
              ) || {};
            return { ...user, profile };
          },
        );
        setUsers(combinedUsers);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải danh sách người dùng:", ex);
      Swal.fire("Lỗi", "Không thể tải dữ liệu", "error");
    } finally {
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreateUser = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...newUser };
      await authApis().post(endpoints["create-user"], payload);
      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Đã tạo người dùng mới",
        timer: 1500,
      });
      setShowCreate(false);
      setNewUser({
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        roles: [],
      });
      loadData();
    } catch (ex) {
      Swal.fire(
        "Lỗi",
        ex.response?.data?.message || "Không thể tạo người dùng",
        "error",
      );
    }
  };

  const openEditModal = (user) => {
    setSelectedUser(user);
    setEditProfile({
      firstName: user.profile?.firstName || "",
      lastName: user.profile?.lastName || "",
      dob: user.profile?.dob || "",
      phoneNumber: user.profile?.phoneNumber || "",
    });
    setEditRoles(user.roles?.map((r) => r.name) || []);
    setAvatarFile(null);
    setShowEdit(true);
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();
    try {
      const api = authApis();
      const profileId = selectedUser.profile?.id;

      if (profileId) {
        await api.patch(endpoints["update-profile"](profileId), editProfile);

        if (avatarFile) {
          const formData = new FormData();
          formData.append("avatar", avatarFile);
          await api.put(endpoints["update-avatar"](profileId), formData, {
            headers: { "Content-Type": "multipart/form-data" },
          });
        }
      }

      await api.put(endpoints["change-role"](selectedUser.id), {
        roles: editRoles,
      });

      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Đã cập nhật thông tin",
        timer: 1500,
      });
      setShowEdit(false);
      loadData();
    } catch (ex) {
      Swal.fire("Lỗi", "Quá trình cập nhật thất bại", "error");
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    try {
      await authApis().put(endpoints["reset-password"](resetData.userId), {
        newPassword: resetData.newPassword,
      });
      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Đã đặt lại mật khẩu",
        timer: 1500,
      });
      setShowReset(false);
      setResetData({ userId: null, newPassword: "" });
    } catch (ex) {
      Swal.fire(
        "Lỗi",
        ex.response?.data?.message || "Không thể đổi mật khẩu",
        "error",
      );
    }
  };

  const handleDeleteUser = async (userId) => {
    const result = await Swal.fire({
      title: "Xác nhận xóa?",
      text: "Bạn không thể hoàn tác hành động này!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Xóa vĩnh viễn",
    });

    if (result.isConfirmed) {
      try {
        await authApis().delete(endpoints["delete-user"](userId));
        Swal.fire("Đã xóa", "Người dùng đã bị xóa khỏi hệ thống.", "success");
        loadData();
      } catch (ex) {
        Swal.fire("Lỗi", "Không thể xóa người dùng này", "error");
      }
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container
      fluid
      className="py-4 px-lg-5"
      style={{ minHeight: "85vh", backgroundColor: "#f8f9fa" }}
    >
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold" style={{ color: "#6C757D" }}>
            Quản lý người dùng
          </h3>
          <p className="text-muted mb-0">
            Hệ thống đang có{" "}
            <strong className="text-primary">{users.length}</strong> tài khoản.
          </p>
        </div>
        <Button
          variant="success"
          className="rounded-pill px-4 shadow-sm"
          onClick={() => setShowCreate(true)}
        >
          + Thêm người dùng
        </Button>
      </div>

      <Card className="border-0 shadow-sm rounded-4 overflow-hidden">
        <Table hover responsive className="mb-0 align-middle">
          <thead className="bg-light text-muted">
            <tr>
              <th className="py-3 px-4 border-0">Người dùng</th>
              <th className="py-3 border-0">Liên hệ</th>
              <th className="py-3 border-0">Vai trò</th>
              <th className="py-3 border-0 text-end px-4">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td className="px-4 py-3">
                  <div className="d-flex align-items-center">
                    {u.profile?.avatar ? (
                      <img
                        src={u.profile.avatar}
                        alt="avatar"
                        className="rounded-circle me-3 object-fit-cover"
                        style={{ width: "45px", height: "45px" }}
                      />
                    ) : (
                      <div
                        className="bg-secondary text-white rounded-circle d-flex justify-content-center align-items-center me-3"
                        style={{ width: "45px", height: "45px" }}
                      >
                        {u.profile?.firstName?.charAt(0) ||
                          u.email.charAt(0).toUpperCase()}
                      </div>
                    )}
                    <div>
                      <div className="fw-bold text-dark">
                        {u.profile?.lastName} {u.profile?.firstName}
                      </div>
                      <div className="text-muted small">{u.email}</div>
                    </div>
                  </div>
                </td>
                <td>
                  <div className="small text-dark">
                    {u.profile?.phoneNumber || (
                      <span className="text-muted fst-italic">
                        Chưa cập nhật
                      </span>
                    )}
                  </div>
                  <div className="text-muted" style={{ fontSize: "0.75rem" }}>
                    {u.profile?.dob
                      ? new Date(u.profile.dob).toLocaleDateString()
                      : ""}
                  </div>
                </td>
                <td>
                  {u.roles?.map((r) => (
                    <Badge
                      key={r.name}
                      bg={
                        r.name === "ADMIN"
                          ? "danger"
                          : r.name === "MANAGER"
                            ? "warning"
                            : "info"
                      }
                      className="me-1"
                    >
                      {r.name}
                    </Badge>
                  ))}
                </td>
                <td className="text-end px-4">
                  <Button
                    variant="light"
                    size="sm"
                    className="me-2 text-primary shadow-sm"
                    onClick={() => openEditModal(u)}
                    title="Chỉnh sửa"
                  >
                    <i className="bi bi-pencil-square"></i> Sửa
                  </Button>
                  <Button
                    variant="light"
                    size="sm"
                    className="me-2 text-warning shadow-sm"
                    onClick={() => {
                      setResetData({ userId: u.id, newPassword: "" });
                      setShowReset(true);
                    }}
                    title="Đổi mật khẩu"
                  >
                    <i className="bi bi-key"></i> Pass
                  </Button>
                  <Button
                    variant="light"
                    size="sm"
                    className="text-danger shadow-sm"
                    onClick={() => handleDeleteUser(u.id)}
                    title="Xóa"
                  >
                    <i className="bi bi-trash"></i> Xóa
                  </Button>
                </td>
              </tr>
            ))}
            {users.length === 0 && (
              <tr>
                <td colSpan="4" className="text-center py-5 text-muted">
                  Không có dữ liệu người dùng.
                </td>
              </tr>
            )}
          </tbody>
        </Table>
      </Card>

      <Modal show={showCreate} onHide={() => setShowCreate(false)} centered>
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">Thêm người dùng mới</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleCreateUser}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Họ</Form.Label>
                  <Form.Control
                    type="text"
                    value={newUser.lastName}
                    onChange={(e) =>
                      setNewUser({ ...newUser, lastName: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Tên</Form.Label>
                  <Form.Control
                    type="text"
                    value={newUser.firstName}
                    onChange={(e) =>
                      setNewUser({ ...newUser, firstName: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold">Email</Form.Label>
              <Form.Control
                type="email"
                value={newUser.email}
                onChange={(e) =>
                  setNewUser({ ...newUser, email: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold">Mật khẩu</Form.Label>
              <Form.Control
                type="text"
                value={newUser.password}
                onChange={(e) =>
                  setNewUser({ ...newUser, password: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold">Vai trò (Roles)</Form.Label>
              <div>
                {roles.map((r) => (
                  <Form.Check
                    inline
                    key={r.name}
                    type="checkbox"
                    label={r.name}
                    checked={newUser.roles.includes(r.name)}
                    onChange={(e) => {
                      const newRoles = e.target.checked
                        ? [...newUser.roles, r.name]
                        : newUser.roles.filter((role) => role !== r.name);
                      setNewUser({ ...newUser, roles: newRoles });
                    }}
                  />
                ))}
              </div>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button variant="light" onClick={() => setShowCreate(false)}>
              Hủy
            </Button>
            <Button variant="success" type="submit">
              Tạo tài khoản
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal
        show={showEdit}
        onHide={() => setShowEdit(false)}
        centered
        size="lg"
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold">Cập nhật thông tin</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleUpdateUser}>
          <Modal.Body>
            <h6 className="fw-bold text-secondary mb-3 border-bottom pb-2">
              1. Thông tin cá nhân
            </h6>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Họ</Form.Label>
                  <Form.Control
                    type="text"
                    value={editProfile.lastName}
                    onChange={(e) =>
                      setEditProfile({
                        ...editProfile,
                        lastName: e.target.value,
                      })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Tên</Form.Label>
                  <Form.Control
                    type="text"
                    value={editProfile.firstName}
                    onChange={(e) =>
                      setEditProfile({
                        ...editProfile,
                        firstName: e.target.value,
                      })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Ngày sinh</Form.Label>
                  <Form.Control
                    type="date"
                    value={editProfile.dob}
                    onChange={(e) =>
                      setEditProfile({ ...editProfile, dob: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">
                    Số điện thoại
                  </Form.Label>
                  <Form.Control
                    type="text"
                    value={editProfile.phoneNumber}
                    onChange={(e) =>
                      setEditProfile({
                        ...editProfile,
                        phoneNumber: e.target.value,
                      })
                    }
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-4">
              <Form.Label className="small fw-bold">
                Ảnh đại diện mới (Bỏ trống nếu không đổi)
              </Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) => setAvatarFile(e.target.files[0])}
              />
            </Form.Group>

            <h6 className="fw-bold text-secondary mb-3 border-bottom pb-2">
              2. Phân quyền Hệ thống
            </h6>
            <Form.Group className="mb-3">
              {roles.map((r) => (
                <Form.Check
                  inline
                  key={r.name}
                  type="checkbox"
                  label={r.name}
                  checked={editRoles.includes(r.name)}
                  onChange={(e) => {
                    const newRoles = e.target.checked
                      ? [...editRoles, r.name]
                      : editRoles.filter((role) => role !== r.name);
                    setEditRoles(newRoles);
                  }}
                />
              ))}
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button variant="light" onClick={() => setShowEdit(false)}>
              Hủy
            </Button>
            <Button variant="primary" type="submit">
              Lưu
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal
        show={showReset}
        onHide={() => setShowReset(false)}
        centered
        size="sm"
      >
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold fs-5">Cấp lại mật khẩu</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleResetPassword}>
          <Modal.Body>
            <Form.Group>
              <Form.Control
                type="text"
                placeholder="Nhập mật khẩu mới..."
                value={resetData.newPassword}
                onChange={(e) =>
                  setResetData({ ...resetData, newPassword: e.target.value })
                }
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button variant="warning" type="submit" className="w-100 fw-bold">
              Xác nhận
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Users;
