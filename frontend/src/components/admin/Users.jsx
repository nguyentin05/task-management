import { useEffect, useState, useRef } from "react";
import {
  Container,
  Card,
  Button,
  Form,
  Table,
  Modal,
  Row,
  Col,
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
  const [isCreating, setIsCreating] = useState(false);
  const [isUpdating, setIsUpdating] = useState(false);

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
      console.error("Lỗi tải dữ liệu:", ex);
      Swal.fire("Lỗi", "Không thể tải dữ liệu người dùng", "error");
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
    if (isCreating) return;

    setIsCreating(true);
    try {
      const payload = { ...newUser };
      await authApis().post(endpoints["create-user"], payload);
      Swal.fire({
        icon: "success",
        title: "Thành công",
        text: "Đã tạo người dùng mới",
        timer: 1000,
      });
      setShowCreate(false);
      setNewUser({
        email: "",
        password: "",
        firstName: "",
        lastName: "",
        roles: [],
      });

      setTimeout(() => {
        loadData();
      }, 500);
    } catch (ex) {
      let errorMessage =
        ex.response?.data?.message || "Không thể tạo người dùng";
      const errorCode = ex.response?.data?.code;

      const fieldLabels = {
        email: "Email",
        password: "Mật khẩu",
        firstName: "Tên",
        lastName: "Họ",
        roles: "Vai trò",
      };

      if (errorCode === 3001 || errorCode === 3004) {
        Object.keys(fieldLabels).forEach((field) => {
          errorMessage = errorMessage.replace(field, fieldLabels[field]);
        });
      }

      Swal.fire("Lỗi", errorMessage, "error");
    } finally {
      setIsCreating(false);
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
    if (isUpdating) return;

    setIsUpdating(true);
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
        timer: 1000,
      });
      setShowEdit(false);
      loadData();
    } catch (ex) {
      let errorMessage =
        ex.response?.data?.message || "Quá trình cập nhật thất bại";
      const errorCode = ex.response?.data?.code;

      const fieldLabels = {
        firstName: "Tên",
        lastName: "Họ",
        dob: "Ngày sinh",
        phoneNumber: "Số điện thoại",
      };

      if (errorCode === 3001 || errorCode === 3004) {
        Object.keys(fieldLabels).forEach((field) => {
          errorMessage = errorMessage.replace(field, fieldLabels[field]);
        });
      }

      Swal.fire("Lỗi", errorMessage, "error");
    } finally {
      setIsUpdating(false);
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
        timer: 1000,
      });
      setShowReset(false);
      setResetData({ userId: null, newPassword: "" });
    } catch (ex) {
      let errorMessage = ex.response?.data?.message || "Không thể đổi mật khẩu";
      const errorCode = ex.response?.data?.code;

      const fieldLabels = {
        newPassword: "Mật khẩu mới",
      };

      if (errorCode === 3001 || errorCode === 3004) {
        Object.keys(fieldLabels).forEach((field) => {
          errorMessage = errorMessage.replace(field, fieldLabels[field]);
        });
      }

      Swal.fire("Lỗi", errorMessage, "error");
    }
  };

  const handleDeleteUser = async (userId) => {
    const result = await Swal.fire({
      title: "Bạn có chắc chắn muốn xóa người dùng này không?",
      text: "Người dùng sẽ bị xóa vĩnh viễn khỏi hệ thống!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#FF5733",
      confirmButtonText: "Xóa",
      cancelButtonColor: "#6C757D",
      cancelButtonText: "Hủy",
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
          className="rounded-pill px-4 shadow-sm"
          style={{
            backgroundColor: "#007BFF",
            borderColor: "#007BFF",
          }}
          onClick={() => setShowCreate(true)}
        >
          Thêm người dùng
        </Button>
      </div>

      <Card className="border-0 shadow-sm rounded-4 overflow-hidden">
        <Table hover responsive className="mb-0 align-middle">
          <thead className="bg-light text-muted">
            <tr>
              <th className="py-3 px-4 border-0">Người dùng</th>
              <th className="py-3 border-0">Thông tin</th>
              <th className="py-3 border-0">Vai trò</th>
              <th className="py-3 border-0 text-end px-4">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td className="px-4 py-3">
                  <div className="d-flex align-items-center">
                    <img
                      src={u.profile?.avatar}
                      alt="avatar"
                      className="rounded-circle me-3 object-fit-cover"
                      style={{ width: "45px", height: "45px" }}
                    />
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
                    <span
                      key={r.name}
                      className="badge me-1 text-white border-0"
                      style={{
                        backgroundColor:
                          r.name === "ADMIN" ? "#FF5733" : "#007BFF",
                      }}
                    >
                      {r.name}
                    </span>
                  ))}
                </td>
                <td className="text-end px-4">
                  {u.email === "admin@gmail.com" ? (
                    <span className="text-muted fst-italic small">
                      Chủ hệ thống
                    </span>
                  ) : (
                    <>
                      <Button
                        variant="light"
                        size="sm"
                        className="me-2 shadow-sm"
                        style={{ color: "#007BFF" }}
                        onClick={() => openEditModal(u)}
                        title="Chỉnh sửa"
                      >
                        <i className="bi bi-pencil-square"></i> Sửa
                      </Button>
                      <Button
                        variant="light"
                        size="sm"
                        className="me-2 shadow-sm"
                        style={{ color: "#FF8C00" }}
                        onClick={() => {
                          setResetData({ userId: u.id, newPassword: "" });
                          setShowReset(true);
                        }}
                        title="Đổi mật khẩu"
                      >
                        <i className="bi bi-key"></i> Đặt lại mật khẩu
                      </Button>
                      <Button
                        variant="light"
                        size="sm"
                        className="shadow-sm"
                        style={{ color: "#FF5733" }}
                        onClick={() => handleDeleteUser(u.id)}
                        title="Xóa"
                      >
                        <i className="bi bi-trash"></i> Xóa
                      </Button>
                    </>
                  )}
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
                  <Form.Label className="small fw-bold">
                    Họ và tên lót
                  </Form.Label>
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
              <Form.Label className="small fw-bold">Vai trò</Form.Label>
              <div>
                {roles.map((r) => (
                  <Form.Check
                    inline
                    key={r.name}
                    type="radio"
                    name="roleRadios_create"
                    label={r.name}
                    checked={newUser.roles.includes(r.name)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setNewUser({ ...newUser, roles: [r.name] });
                      }
                    }}
                  />
                ))}
              </div>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button
              style={{
                backgroundColor: "#6C757D",
                borderColor: "#6C757D",
              }}
              onClick={() => setShowCreate(false)}
            >
              Hủy
            </Button>
            <Button
              type="submit"
              disabled={isCreating}
              style={{
                backgroundColor: "#28A745",
                borderColor: "#28A745",
              }}
            >
              {isCreating ? "Đang xử lý..." : "Tạo"}
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
              Thông tin cá nhân
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
              Vai trò
            </h6>
            <Form.Group className="mb-3">
              {roles.map((r) => (
                <Form.Check
                  inline
                  key={r.name}
                  type="radio"
                  name="roleRadios_edit"
                  label={r.name}
                  checked={editRoles.includes(r.name)}
                  onChange={(e) => {
                    if (e.target.checked) {
                      setEditRoles([r.name]);
                    }
                  }}
                />
              ))}
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button
              style={{
                backgroundColor: "#6C757D",
                borderColor: "#6C757D",
              }}
              onClick={() => setShowEdit(false)}
            >
              Hủy
            </Button>
            <Button
              type="submit"
              disabled={isUpdating}
              style={{
                backgroundColor: "#28A745",
                borderColor: "#28A745",
              }}
            >
              {isUpdating ? "Đang lưu..." : "Lưu"}
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
          <Modal.Title className="fw-bold fs-5">Đặt lại mật khẩu</Modal.Title>
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
            <Button
              type="submit"
              className="w-100 fw-bold"
              style={{
                backgroundColor: "#28A745",
                borderColor: "#28A745",
              }}
            >
              Xác nhận
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Users;
