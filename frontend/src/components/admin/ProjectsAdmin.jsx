import { useEffect, useState } from "react";
import { Table, Button, Modal, Alert, Pagination } from "react-bootstrap";
import { authApis } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";

const ProjectsAdmin = () => {
  const [projects, setProjects] = useState([]);
  const [detail, setDetail] = useState(null);

  const [page, setPage] = useState(0);
  const [size] = useState(15);

  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");

  const [showDetail, setShowDetail] = useState(false);

  const loadProjects = async () => {
    try {
      setLoading(true);

      let res = await authApis().get("/projects", {
        params: { page, size },
      });

      setProjects(res.data.result);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, [page]);

  const loadDetail = async (id) => {
    let res = await authApis().get(`/projects/${id}`);
    setDetail(res.data.result);
    setShowDetail(true);
  };

  const deleteProject = async (id) => {
    if (!window.confirm("Xóa project?")) return;

    await authApis().delete(`/projects/${id}`);

    setMsg("Xóa project thành công");
    loadProjects();
  };

  if (loading) return <MySpinner />;

  return (
    <div>
      <h2>Quản lý Projects</h2>

      {msg && <Alert variant="success">{msg}</Alert>}

      <Table bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Người tạo</th>
            <th>Bắt đầu</th>
            <th>Kết thúc</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {projects.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.createdBy}</td>
              <td>{p.startAt}</td>
              <td>{p.endAt}</td>

              <td>
                <Button
                  size="sm"
                  variant="info"
                  onClick={() => loadDetail(p.id)}
                >
                  Chi tiết
                </Button>

                <Button
                  size="sm"
                  variant="danger"
                  className="mx-2"
                  onClick={() => deleteProject(p.id)}
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

      <Modal show={showDetail} onHide={() => setShowDetail(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Chi tiết Project</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {detail && (
            <>
              <p>
                <b>Tên:</b> {detail.name}
              </p>
              <p>
                <b>Mô tả:</b> {detail.description}
              </p>
              <p>
                <b>Bắt đầu:</b> {detail.startAt}
              </p>
              <p>
                <b>Kết thúc:</b> {detail.endAt}
              </p>
              <p>
                <b>Tạo lúc:</b> {detail.createdAt}
              </p>
            </>
          )}
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default ProjectsAdmin;
