import { useEffect, useState, useRef } from "react";
import {
  Container,
  Card,
  Table,
  Button,
  Modal,
  Form,
  Row,
  Col,
  Badge,
} from "react-bootstrap";
import { useParams, Link } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layout/MySpinner";
import Swal from "sweetalert2";

const Projects = () => {
  const { workspaceId } = useParams();
  const [loading, setLoading] = useState(true);
  const loadingStartTime = useRef(null);

  const [projects, setProjects] = useState([]);
  const [workspaceInfo, setWorkspaceInfo] = useState(null);

  const [showEdit, setShowEdit] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [editData, setEditData] = useState({
    name: "",
    description: "",
    startAt: "",
    endAt: "",
  });

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
      const [wsRes, pjRes] = await Promise.all([
        api.get(endpoints["get-workspaces"](workspaceId)),
        api.get(endpoints["get-projects-in-workspace"](workspaceId)),
      ]);

      await ensureSpinnerMinTime();

      if (wsRes.data.code === 1000) setWorkspaceInfo(wsRes.data.result);

      if (pjRes.data.code === 1000) {
        const data = pjRes.data.result.data || pjRes.data.result;
        setProjects(Array.isArray(data) ? data : []);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải dữ liệu:", ex);
      Swal.fire("Lỗi", "Không thể tải danh sách dự án", "error");
    } finally {
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  useEffect(() => {
    if (workspaceId) loadData();
  }, [workspaceId]);

  const openEditModal = (pj) => {
    setSelectedProject(pj);
    setEditData({
      name: pj.name,
      description: pj.description || "",
      startAt: pj.startAt
        ? new Date(pj.startAt).toISOString().slice(0, 16)
        : "",
      endAt: pj.endAt ? new Date(pj.endAt).toISOString().slice(0, 16) : "",
    });
    setShowEdit(true);
  };

  const handleUpdateProject = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...editData,
        startAt: new Date(editData.startAt).toISOString(),
        endAt: new Date(editData.endAt).toISOString(),
      };

      const res = await authApis().patch(
        endpoints["update-project"](selectedProject.id),
        payload,
      );

      if (res.data.code === 1000) {
        Swal.fire({
          icon: "success",
          title: "Thành công",
          text: "Đã cập nhật dự án",
          timer: 1000,
        });
        setShowEdit(false);
        loadData();
      }
    } catch (ex) {
      const errorMessage =
        ex.response?.data?.message || "Cập nhật dự án thất bại!";
      Swal.fire("Lỗi", errorMessage, "error");
    }
  };

  const handleDeleteProject = async (projectId) => {
    const result = await Swal.fire({
      title: "Bạn có chắc chắn muốn xóa dự án này không?",
      text: "Dự án sẽ bị xóa vĩnh viễn khỏi hệ thống!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#FF5733",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
      cancelButtonColor: "#6C757D",
    });

    if (result.isConfirmed) {
      try {
        const res = await authApis().delete(
          endpoints["delete-project"](projectId),
        );
        if (res.data.code === 1000) {
          setProjects(projects.filter((p) => p.id !== projectId));
          Swal.fire("Đã xóa", "Dự án đã được xóa vĩnh viễn.", "success");
        }
      } catch (ex) {
        Swal.fire("Lỗi", "Không thể xóa dự án này!", "error");
      }
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container
      fluid
      className="py-4 px-lg-5"
      style={{ minHeight: "85vh", backgroundColor: "#F8F9FA" }}
    >
      <div className="mb-4">
        <Link
          to="/admin/workspaces"
          className="text-decoration-none small text-primary fw-bold"
        >
          Quay lại
        </Link>
        <h3 className="fw-bold mt-2" style={{ color: "#6C757D" }}>
          Không gian làm việc:{" "}
          <span className="text-dark">{workspaceInfo?.name}</span>
        </h3>
        <p className="text-muted small">ID: {workspaceId}</p>
      </div>

      <Card className="border-0 shadow-sm rounded-4 overflow-hidden">
        <Table hover responsive className="mb-0 align-middle">
          <thead className="bg-light text-muted">
            <tr>
              <th className="py-3 px-4 border-0">Tên dự án</th>
              <th className="py-3 border-0">Thời gian thực hiện</th>
              <th className="py-3 border-0 text-end px-4">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {projects.map((p) => (
              <tr key={p.id}>
                <td className="px-4 py-3">
                  <div className="fw-bold text-dark">{p.name}</div>
                  <div
                    className="text-muted small text-truncate"
                    style={{ maxWidth: "250px" }}
                  >
                    {p.description || "Không có mô tả"}
                  </div>
                </td>
                <td>
                  <div className="small">
                    <Badge bg="light" text="dark" className="border">
                      Bắt đầu: {new Date(p.startAt).toLocaleDateString()}
                    </Badge>
                  </div>
                  <div className="small mt-1">
                    <Badge
                      bg="light"
                      className="border"
                      style={{ color: "#FF5733" }}
                    >
                      Kết thúc: {new Date(p.endAt).toLocaleDateString()}
                    </Badge>
                  </div>
                </td>
                <td className="text-end px-4">
                  <Button
                    variant="light"
                    size="sm"
                    className="me-2 shadow-sm"
                    style={{ color: "#FF8C00" }}
                    onClick={() => openEditModal(p)}
                  >
                    Sửa
                  </Button>
                  <Button
                    as={Link}
                    to={`/p/${p.id}`}
                    variant="light"
                    size="sm"
                    className="me-2 shadow-sm"
                    style={{ color: "#007BFF" }}
                  >
                    Xem
                  </Button>
                  <Button
                    variant="light"
                    size="sm"
                    className="text-danger shadow-sm"
                    style={{ color: "#FF5733" }}
                    onClick={() => handleDeleteProject(p.id)}
                  >
                    Xóa
                  </Button>
                </td>
              </tr>
            ))}
            {projects.length === 0 && (
              <tr>
                <td colSpan="4" className="text-center py-5 text-muted">
                  Không gian làm việc hiện chưa có dự án nào.
                </td>
              </tr>
            )}
          </tbody>
        </Table>
      </Card>

      <Modal
        show={showEdit}
        onHide={() => setShowEdit(false)}
        centered
        size="lg"
      >
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">Chỉnh sửa dự án</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleUpdateProject}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold">Tên</Form.Label>
              <Form.Control
                type="text"
                value={editData.name}
                onChange={(e) =>
                  setEditData({ ...editData, name: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold">Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={editData.description}
                onChange={(e) =>
                  setEditData({ ...editData, description: e.target.value })
                }
              />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Bắt đầu</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={editData.startAt}
                    onChange={(e) =>
                      setEditData({ ...editData, startAt: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="small fw-bold">Kết thúc</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={editData.endAt}
                    onChange={(e) =>
                      setEditData({ ...editData, endAt: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
            </Row>
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
              style={{
                backgroundColor: "#28A745",
                borderColor: "#28A745",
              }}
            >
              Lưu
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Projects;
