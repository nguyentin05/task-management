import { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Alert, Pagination } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";

const Workspaces = () => {
  const [workspaces, setWorkspaces] = useState([]);
  const [projects, setProjects] = useState([]);

  const [selected, setSelected] = useState(null);

  const [workspaceEdit, setWorkspaceEdit] = useState({
    name: "",
    description: "",
  });

  const [showDetail, setShowDetail] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [showProjects, setShowProjects] = useState(false);

  const [page, setPage] = useState(0);
  const [size] = useState(10);

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const loadWorkspaces = async () => {
    try {
      setLoading(true);

      let res = await authApis().get(endpoints["workspaces"], {
        params: {
          page: page,
          size: size,
        },
      });

      setWorkspaces(res.data.result);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadWorkspaces();
  }, [page]);

  const loadWorkspaceDetail = async (id) => {
    try {
      setLoading(true);

      let res = await authApis().get(`${endpoints["workspaces"]}/${id}`);

      setSelected(res.data.result);

      setWorkspaceEdit({
        name: res.data.result.name,
        description: res.data.result.description,
      });

      setShowDetail(true);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  const loadProjects = async (id) => {
    try {
      setLoading(true);

      let res = await authApis().get(
        `${endpoints["workspaces"]}/${id}/projects`,
      );

      setProjects(res.data.result);
      setShowProjects(true);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  const updateWorkspace = async (e) => {
    e.preventDefault();

    try {
      setLoading(true);

      await authApis().patch(
        `${endpoints["workspaces"]}/${selected.id}`,
        workspaceEdit,
      );

      setShowEdit(false);

      setMsg("Cập nhật workspace thành công");

      loadWorkspaces();
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <div>
      <h2>Quản lý Workspace</h2>

      {msg && <Alert variant="success">{msg}</Alert>}

      <Table bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>UID</th>
            <th>Tên</th>
            <th>Mô tả</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {workspaces.map((w) => (
            <tr key={w.id}>
              <td>{w.id}</td>
              <td>{w.userId}</td>
              <td>{w.name}</td>
              <td>{w.description}</td>

              <td>
                <Button
                  size="sm"
                  variant="info"
                  onClick={() => loadWorkspaceDetail(w.id)}
                >
                  Chi tiết
                </Button>

                <Button
                  size="sm"
                  className="mx-2"
                  variant="warning"
                  onClick={() => {
                    setSelected(w);
                    setWorkspaceEdit({
                      name: w.name,
                      description: w.description,
                    });
                    setShowEdit(true);
                  }}
                >
                  Edit
                </Button>

                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() => loadProjects(w.id)}
                >
                  Projects
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

      <Modal show={showDetail} onHide={() => setShowDetail(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Chi tiết Workspace</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {selected && (
            <>
              <p>
                <b>ID:</b> {selected.id}
              </p>
              <p>
                <b>UID:</b> {selected.userId}
              </p>
              <p>
                <b>Tên:</b> {selected.name}
              </p>
              <p>
                <b>Mô tả:</b> {selected.description}
              </p>
            </>
          )}
        </Modal.Body>
      </Modal>

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
            <Button variant="secondary" onClick={() => setShowEdit(false)}>
              Hủy
            </Button>

            <Button type="submit" variant="success">
              Lưu
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal
        size="lg"
        show={showProjects}
        onHide={() => setShowProjects(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Projects</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Table bordered>
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Mô tả</th>
              </tr>
            </thead>

            <tbody>
              {projects.map((p) => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.name}</td>
                  <td>{p.description}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default Workspaces;
