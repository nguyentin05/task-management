import { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Alert, Pagination } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";

const MyWorkspace = () => {
  const [workspace, setWorkspace] = useState(null);
  const [projects, setProjects] = useState([]);

  const [workspaceEdit, setWorkspaceEdit] = useState({
    name: "",
    description: "",
  });

  const [showEdit, setShowEdit] = useState(false);

  const [page, setPage] = useState(0);
  const [size] = useState(10);

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const loadWorkspace = async () => {
    try {
      setLoading(true);

      let res = await authApis().get("/workspaces/me");

      setWorkspace(res.data.result);

      setWorkspaceEdit({
        name: res.data.result.name,
        description: res.data.result.description,
      });
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  const loadProjects = async () => {
    try {
      setLoading(true);

      let res = await authApis().get("/workspaces/me/projects", {
        params: {
          page: page,
          size: size,
        },
      });

      setProjects(res.data.result);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadWorkspace();
    loadProjects();
  }, [page]);

  const updateWorkspace = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      await authApis().patch("/workspaces/me", workspaceEdit);

      setMsg("Cập nhật workspace thành công");

      setShowEdit(false);

      loadWorkspace();
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  const removeProject = async (projectId) => {
    if (!window.confirm("Bạn có chắc muốn rời project này?")) return;

    try {
      setLoading(true);

      await authApis().delete(`/workspaces/me/projects/${projectId}`);

      setMsg("Xóa project khỏi workspace thành công");

      loadProjects();
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <div>
      <h2>Workspace của tôi</h2>

      {msg && <Alert variant="success">{msg}</Alert>}

      {workspace && (
        <div className="mb-4">
          <h4>Thông tin workspace</h4>

          <p>
            <b>ID:</b> {workspace.id}
          </p>
          <p>
            <b>Tên:</b> {workspace.name}
          </p>
          <p>
            <b>Mô tả:</b> {workspace.description}
          </p>

          <Button variant="warning" onClick={() => setShowEdit(true)}>
            Chỉnh sửa workspace
          </Button>
        </div>
      )}

      <h4>Projects trong workspace</h4>

      <Table bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Mô tả</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {projects.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.description}</td>

              <td>
                <Button
                  variant="danger"
                  size="sm"
                  onClick={() => removeProject(p.id)}
                >
                  Rời Project
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

      <Modal show={showEdit} onHide={() => setShowEdit(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh sửa Workspace</Modal.Title>
        </Modal.Header>

        <Form onSubmit={updateWorkspace}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Tên</Form.Label>

              <Form.Control
                value={workspaceEdit.name}
                onChange={(e) =>
                  setWorkspaceEdit({
                    ...workspaceEdit,
                    name: e.target.value,
                  })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Mô tả</Form.Label>

              <Form.Control
                value={workspaceEdit.description}
                onChange={(e) =>
                  setWorkspaceEdit({
                    ...workspaceEdit,
                    description: e.target.value,
                  })
                }
              />
            </Form.Group>
          </Modal.Body>

          <Modal.Footer>
            <Button variant="danger" onClick={() => setShowEdit(false)}>
              Hủy
            </Button>

            <Button type="submit" variant="success">
              Lưu
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};

export default MyWorkspace;
