import { useEffect, useState } from "react";
import { Button, Form, Table, Modal } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const Roles = () => {
  const [roles, setRoles] = useState([]);
  const [permissions, setPermissions] = useState([]);

  const [showCreate, setShowCreate] = useState(false);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [selectedPermissions, setSelectedPermissions] = useState([]);

  const loadRoles = async () => {
    let res = await authApis().get(endpoints["roles"], {
      params: {
        page: 0,
        size: 10,
      },
    });

    setRoles(res.data.result);
  };

  const loadPermissions = async () => {
    let res = await authApis().get(endpoints["permissions"]);
    setPermissions(res.data.result);
  };

  useEffect(() => {
    loadRoles();
    loadPermissions();
  }, []);

  const handlePermissionChange = (perm) => {
    if (selectedPermissions.includes(perm))
      setSelectedPermissions(selectedPermissions.filter((p) => p !== perm));
    else setSelectedPermissions([...selectedPermissions, perm]);
  };

  const createRole = async (e) => {
    e.preventDefault();

    await authApis().post(endpoints["roles"], {
      name,
      description,
      permissions: selectedPermissions,
    });

    setName("");
    setDescription("");
    setSelectedPermissions([]);

    setShowCreate(false);

    loadRoles();
  };

  const deleteRole = async (roleName) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa role này không?")) {
      await authApis().delete(`${endpoints["roles"]}/${roleName}`);
      loadRoles();
    }
  };

  return (
    <div>
      <h2 className="mb-3">Role</h2>

      <Button
        variant="success"
        className="mb-3"
        onClick={() => setShowCreate(true)}
      >
        Tạo Role
      </Button>

      <Table bordered>
        <thead>
          <tr>
            <th>Tên role</th>
            <th>Mô tả</th>
            <th>Quyền</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {roles.map((r) => (
            <tr key={r.name}>
              <td>{r.name}</td>

              <td>{r.description}</td>

              <td>{r.permissions?.map((p) => p.name).join(", ")}</td>

              <td>
                <Button variant="danger" onClick={() => deleteRole(r.name)}>
                  Xóa
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showCreate} onHide={() => setShowCreate(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo Role</Modal.Title>
        </Modal.Header>

        <Form onSubmit={createRole}>
          <Modal.Body>
            <Form.Control
              className="mb-2"
              placeholder="Nhập tên role"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />

            <Form.Control
              className="mb-3"
              placeholder="Mô tả"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />

            <Form.Label>Quyền</Form.Label>

            {permissions.map((p) => (
              <Form.Check
                key={p.name}
                label={p.name}
                checked={selectedPermissions.includes(p.name)}
                onChange={() => handlePermissionChange(p.name)}
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
    </div>
  );
};

export default Roles;
