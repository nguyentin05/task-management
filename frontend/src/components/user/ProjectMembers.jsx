import { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Alert } from "react-bootstrap";
import { authApis } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";

const ProjectMembers = ({ projectId }) => {
  const [members, setMembers] = useState([]);

  const [showAdd, setShowAdd] = useState(false);
  const [showRole, setShowRole] = useState(false);

  const [selected, setSelected] = useState(null);

  const [member, setMember] = useState({
    email: "",
    role: "MEMBER",
  });

  const [roleUpdate, setRoleUpdate] = useState("MEMBER");

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const loadMembers = async () => {
    try {
      setLoading(true);

      let res = await authApis().get(`/projects/${projectId}/members`);

      setMembers(res.data.result);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMembers();
  }, []);

  const addMember = async (e) => {
    e.preventDefault();

    try {
      await authApis().post(`/projects/${projectId}/members`, member);

      setMsg("Thêm thành viên thành công");

      setMember({
        email: "",
        role: "MEMBER",
      });

      setShowAdd(false);

      loadMembers();
    } catch (ex) {
      console.error(ex);
    }
  };

  const updateRole = async () => {
    try {
      await authApis().put(
        `/projects/${projectId}/members/${selected.userId}`,
        { role: roleUpdate },
      );

      setMsg("Cập nhật quyền thành công");

      setShowRole(false);

      loadMembers();
    } catch (ex) {
      console.error(ex);
    }
  };

  const deleteMember = async (userId) => {
    if (!window.confirm("Xóa thành viên khỏi project?")) return;

    try {
      await authApis().delete(`/projects/${projectId}/members/${userId}`);

      setMsg("Xóa thành viên thành công");

      loadMembers();
    } catch (ex) {
      console.error(ex);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <div>
      <h4>Thành viên dự án</h4>

      {msg && <Alert variant="success">{msg}</Alert>}

      <Button className="mb-3" onClick={() => setShowAdd(true)}>
        Thêm thành viên
      </Button>

      <Table bordered hover>
        <thead>
          <tr>
            <th>UID</th>
            <th>Email</th>
            <th>Vai trò</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {members.map((m) => (
            <tr key={m.userId}>
              <td>{m.userId}</td>
              <td>{m.email}</td>
              <td>{m.role}</td>

              <td>
                <Button
                  size="sm"
                  variant="warning"
                  onClick={() => {
                    setSelected(m);
                    setRoleUpdate(m.role);
                    setShowRole(true);
                  }}
                >
                  Đổi vai trò
                </Button>

                <Button
                  size="sm"
                  className="mx-2"
                  variant="danger"
                  onClick={() => deleteMember(m.userId)}
                >
                  Xóa
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showAdd} onHide={() => setShowAdd(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Thêm thành viên</Modal.Title>
        </Modal.Header>

        <Form onSubmit={addMember}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>

              <Form.Control
                type="email"
                value={member.email}
                onChange={(e) =>
                  setMember({
                    ...member,
                    email: e.target.value,
                  })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Vai trò</Form.Label>

              <Form.Select
                value={member.role}
                onChange={(e) =>
                  setMember({
                    ...member,
                    role: e.target.value,
                  })
                }
              >
                <option value="MEMBER">Thành viên</option>

                <option value="MANAGER">Quản lý</option>
              </Form.Select>
            </Form.Group>
          </Modal.Body>

          <Modal.Footer>
            <Button variant="danger" onClick={() => setShowAdd(false)}>
              Hủy
            </Button>

            <Button type="submit" variant="success">
              Thêm
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal show={showRole} onHide={() => setShowRole(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Cập nhật vai trò</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form.Select
            value={roleUpdate}
            onChange={(e) => setRoleUpdate(e.target.value)}
          >
            <option value="MEMBER">Thành viên</option>

            <option value="MANAGER">Quản lý</option>
          </Form.Select>
        </Modal.Body>

        <Modal.Footer>
          <Button variant="danger" onClick={() => setShowRole(false)}>
            Hủy
          </Button>

          <Button variant="success" onClick={updateRole}>
            Lưu
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ProjectMembers;
