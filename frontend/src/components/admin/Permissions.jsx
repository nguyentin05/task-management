import { useEffect, useState } from "react";
import { Button, Form, Table, Alert, Pagination, Modal } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";

const Permissions = () => {
  const [permissions, setPermissions] = useState([]);

  const [perm, setPerm] = useState({
    name: "",
    description: "",
  });

  const [showModal, setShowModal] = useState(false);

  const [page, setPage] = useState(0);
  const [size] = useState(10);

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const loadPermissions = async () => {
    try {
      setLoading(true);

      let res = await authApis().get(endpoints["permissions"], {
        params: {
          page: page,
          size: size,
        },
      });

      setPermissions(res.data.result);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPermissions();
  }, [page]);

  const createPermission = async (e) => {
    e.preventDefault();

    if (!perm.name.trim()) {
      setMsg("Tên quyền không được để trống");
      return;
    }

    try {
      setLoading(true);

      await authApis().post(endpoints["permissions"], perm);

      setPerm({
        name: "",
        description: "",
      });

      setShowModal(false);

      setMsg("Tạo quyền thành công");

      loadPermissions();
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  const deletePermission = async (name) => {
    if (!window.confirm("Bạn có chắc muốn xóa quyền này không?")) return;

    try {
      setLoading(true);

      await authApis().delete(`${endpoints["permissions"]}/${name}`);

      setMsg("Xóa quyền thành công");

      loadPermissions();
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <div>
      <h2>Quản lý quyền</h2>

      {msg && <Alert variant="success">{msg}</Alert>}

      <Button
        variant="success"
        className="mb-3"
        onClick={() => setShowModal(true)}
      >
        Tạo quyền
      </Button>

      <Table bordered hover>
        <thead>
          <tr>
            <th>Tên quyền</th>
            <th>Mô tả</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {permissions.map((p) => (
            <tr key={p.name}>
              <td>{p.name}</td>
              <td>{p.description}</td>
              <td>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => deletePermission(p.name)}
                >
                  Xóa
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Pagination>
        <Pagination.Prev
          disabled={page === 0}
          onClick={() => setPage(page - 1)}
        />

        <Pagination.Item active>{page + 1}</Pagination.Item>

        <Pagination.Next onClick={() => setPage(page + 1)} />
      </Pagination>

      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo quyền</Modal.Title>
        </Modal.Header>

        <Form onSubmit={createPermission}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Tên quyền</Form.Label>
              <Form.Control
                placeholder="Nhập tên quyền"
                value={perm.name}
                onChange={(e) => setPerm({ ...perm, name: e.target.value })}
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                placeholder="Nhập mô tả"
                value={perm.description}
                onChange={(e) =>
                  setPerm({ ...perm, description: e.target.value })
                }
              />
            </Form.Group>
          </Modal.Body>

          <Modal.Footer>
            <Button variant="danger" onClick={() => setShowModal(false)}>
              Hủy
            </Button>

            <Button type="submit" variant="success">
              Tạo
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};

export default Permissions;
