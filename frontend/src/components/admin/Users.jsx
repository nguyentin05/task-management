import { useEffect, useState } from "react";
import { Button, Form, Table, Modal } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const Users = () => {
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);

  const [showCreate, setShowCreate] = useState(false);
  const [showReset, setShowReset] = useState(false);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");

  const [selectedRoles, setSelectedRoles] = useState([]);

  const [resetUserId, setResetUserId] = useState(null);
  const [newPassword, setNewPassword] = useState("");

  const loadUsers = async () => {
    let res = await authApis().get(endpoints["users"], {
      params: { page: 0, size: 15 },
    });

    setUsers(res.data.result);
  };

  const loadRoles = async () => {
    let res = await authApis().get(endpoints["roles"]);
    setRoles(res.data.result);
  };

  useEffect(() => {
    loadUsers();
    loadRoles();
  }, []);

  const handleRoleChange = (role) => {
    if (selectedRoles.includes(role))
      setSelectedRoles(selectedRoles.filter((r) => r !== role));
    else setSelectedRoles([...selectedRoles, role]);
  };

  const createUser = async (e) => {
    e.preventDefault();

    await authApis().post(endpoints["users"], {
      email,
      password,
      firstName,
      lastName,
      roles: selectedRoles,
    });

    setShowCreate(false);

    setEmail("");
    setPassword("");
    setFirstName("");
    setLastName("");
    setSelectedRoles([]);

    loadUsers();
  };

  const deleteUser = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa người dùng này không?")) {
      await authApis().delete(`${endpoints["users"]}/${id}`);
      loadUsers();
    }
  };

  const openReset = (id) => {
    setResetUserId(id);
    setShowReset(true);
  };

  const resetPassword = async () => {
    await authApis().put(
      `${endpoints["users"]}/${resetUserId}/reset-password`,
      {
        newPassword,
      },
    );

    setShowReset(false);
    setNewPassword("");
  };

  return (
    <div>
      <h2 className="mb-3">Quản lý người dùng</h2>

      <Button
        variant="success"
        className="mb-3"
        onClick={() => setShowCreate(true)}
      >
        Thêm người dùng
      </Button>

      <Table bordered>
        <thead>
          <tr>
            <th>Email</th>
            <th>Role</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.email}</td>

              <td>{u.roles?.map((r) => r.name).join(", ")}</td>

              <td>
                <Button
                  variant="warning"
                  className="me-2"
                  onClick={() => openReset(u.id)}
                >
                  Đổi mật khẩu
                </Button>

                <Button variant="danger" onClick={() => deleteUser(u.id)}>
                  Xóa
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showCreate} onHide={() => setShowCreate(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Thêm người dùng</Modal.Title>
        </Modal.Header>

        <Form onSubmit={createUser}>
          <Modal.Body>
            <Form.Control
              className="mb-2"
              placeholder="Nhập email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

            <Form.Control
              className="mb-2"
              type="password"
              placeholder="Nhập mật khẩu"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <Form.Control
              className="mb-2"
              placeholder="Nhập họ"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              required
            />

            <Form.Control
              className="mb-2"
              placeholder="Nhập tên"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              required
            />

            <Form.Label>Role</Form.Label>

            {roles.map((r) => (
              <Form.Check
                key={r.name}
                label={r.name}
                checked={selectedRoles.includes(r.name)}
                onChange={() => handleRoleChange(r.name)}
              />
            ))}
          </Modal.Body>

          <Modal.Footer>
            <Button variant="danger" onClick={() => setShowCreate(false)}>
              Hủy
            </Button>

            <Button type="submit">Tạo</Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal show={showReset} onHide={() => setShowReset(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Đổi mật khẩu</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form.Control
            type="password"
            placeholder="Nhập mật khẩu mới"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </Modal.Body>

        <Modal.Footer>
          <Button onClick={resetPassword}>Xác nhận</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default Users;
