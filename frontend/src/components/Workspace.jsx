import { useEffect, useState, useRef } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Modal,
  Form,
} from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layout/MySpinner";
import Swal from "sweetalert2";
import { Link } from "react-router-dom";

const WorkSpace = () => {
  const [workspace, setWorkspace] = useState(null);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadingStartTime = useRef(null);

  const [showEditWS, setShowEditWS] = useState(false);
  const [showAddProject, setShowAddProject] = useState(false);
  const [editWSData, setEditWSData] = useState({ name: "", description: "" });
  const [newProject, setNewProject] = useState({
    name: "",
    description: "",
    startAt: "",
    endAt: "",
  });
  const [showEditProject, setShowEditProject] = useState(false);
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [editProjectData, setEditProjectData] = useState({
    name: "",
    description: "",
    startAt: "",
    endAt: "",
  });

  const ensureSpinnerMinTime = () => {
    if (!loadingStartTime.current) return Promise.resolve();
    const displayTime = Date.now() - loadingStartTime.current;
    const minDisplay = 500;
    if (displayTime < minDisplay) {
      return new Promise((resolve) =>
        setTimeout(resolve, minDisplay - displayTime),
      );
    }
    return Promise.resolve();
  };

  const loadData = async () => {
    const delayTimer = setTimeout(() => {
      setLoading(true);
      loadingStartTime.current = Date.now();
    }, 300);

    try {
      const api = authApis();
      const [wsRes, pjRes] = await Promise.all([
        api.get(endpoints["workspaces-me"]),
        api.get(endpoints["workspaces-me-projects"]),
      ]);

      await ensureSpinnerMinTime();

      if (wsRes.data.code === 1000) {
        setWorkspace(wsRes.data.result);
        setEditWSData({
          name: wsRes.data.result.name,
          description: wsRes.data.result.description,
        });
      }

      if (pjRes.data.code === 1000) {
        const data = pjRes.data.result.data || pjRes.data.result;
        setProjects(Array.isArray(data) ? data : []);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải dữ liệu:", ex);
    } finally {
      clearTimeout(delayTimer);
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleUpdateWS = async (e) => {
    e.preventDefault();
    try {
      const res = await authApis().patch(
        endpoints["workspaces-me"],
        editWSData,
      );
      if (res.data.code === 1000) {
        setWorkspace(res.data.result);
        setShowEditWS(false);
        Swal.fire({
          icon: "success",
          title: "Thành công",
          text: "Không gian làm việc đã được cập nhật!",
          timer: 1000,
          showConfirmButton: false,
        });
      }
    } catch (ex) {
      let errorMessage =
        ex.response?.data?.message || "Không thể cập nhật thông tin!";
      const errorCode = ex.response?.data?.code;

      const fieldLabels = {
        name: "Tên không gian",
        description: "Mô tả",
      };

      if (errorCode === 3004) {
        Object.keys(fieldLabels).forEach((field) => {
          errorMessage = errorMessage.replace(field, fieldLabels[field]);
        });
      }

      Swal.fire("Lỗi", errorMessage, "error");
    }
  };

  const handleCreateProject = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...newProject,
        startAt: new Date(newProject.startAt).toISOString(),
        endAt: new Date(newProject.endAt).toISOString(),
      };

      const res = await authApis().post(endpoints["create-project"], payload);

      if (res.data.code === 1000) {
        setProjects([res.data.result, ...projects]);
        setShowAddProject(false);
        setNewProject({ name: "", description: "", startAt: "", endAt: "" });
        Swal.fire({
          icon: "success",
          title: "Thành công",
          text: "Dự án mới đã được tạo!",
          timer: 1000,
          showConfirmButton: false,
        });
      }
    } catch (ex) {
      let errorMessage = ex.response?.data?.message || "Không thể tạo dự án!";
      const errorCode = ex.response?.data?.code;

      const fieldLabels = {
        name: "Tên dự án",
        description: "Mô tả",
        startAt: "Thời gian bắt đầu",
        endAt: "Thời gian kết thúc",
      };

      if (errorCode === 3001 || errorCode === 3004) {
        Object.keys(fieldLabels).forEach((field) => {
          errorMessage = errorMessage.replace(field, fieldLabels[field]);
        });
      }

      Swal.fire("Lỗi", errorMessage, "error");
    }
  };

  const openEditProjectModal = (p, e) => {
    setSelectedProjectId(p.id);
    setEditProjectData({
      name: p.name || "",
      description: p.description || "",
      startAt: p.startAt ? p.startAt.slice(0, 16) : "",
      endAt: p.endAt ? p.endAt.slice(0, 16) : "",
    });
    setShowEditProject(true);
  };

  const handleUpdateProject = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...editProjectData };
      if (payload.startAt)
        payload.startAt = new Date(payload.startAt).toISOString();
      if (payload.endAt) payload.endAt = new Date(payload.endAt).toISOString();

      const res = await authApis().patch(
        endpoints["update-project"](selectedProjectId),
        payload,
      );

      if (res.data.code === 1000) {
        loadData();
        setShowEditProject(false);
        Swal.fire({
          icon: "success",
          title: "Thành công",
          text: "Đã cập nhật dự án!",
          timer: 1000,
          showConfirmButton: false,
        });
      }
    } catch (ex) {
      let errorMessage =
        ex.response?.data?.message || "Cập nhật dự án thất bại!";
      const errorCode = ex.response?.data?.code;

      const fieldLabels = {
        name: "Tên dự án",
        description: "Mô tả",
        startAt: "Thời gian bắt đầu",
        endAt: "Thời gian kết thúc",
      };

      if (errorCode === 3001 || errorCode === 3004) {
        Object.keys(fieldLabels).forEach((field) => {
          errorMessage = errorMessage.replace(field, fieldLabels[field]);
        });
      }

      Swal.fire("Lỗi", errorMessage, "error");
    }
  };

  const handleDeleteProject = async (projectId, e) => {
    e.preventDefault();

    const result = await Swal.fire({
      title: "Xác nhận?",
      text: "Bạn có chắc chắn muốn xóa dự án này không?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#FF5733",
      cancelButtonColor: "#6C757D",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      try {
        const res = await authApis().delete(
          endpoints["delete-workspaces-me-projects"](projectId),
        );
        if (res.data.code === 1000) {
          setProjects(projects.filter((p) => p.id !== projectId));
          Swal.fire("Thành công", "Đã xóa dự án", "success");
        }
      } catch (ex) {
        let errorMessage =
          ex.response?.data?.message || "Có lỗi xảy ra, vui lòng thử lại sau!";
        Swal.fire("Lỗi", errorMessage, "error");
      }
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container className="mt-4 pb-5">
      <Card
        className="mb-5 shadow-sm border-0 p-4 rounded-4"
        style={{ backgroundColor: "#F8F9FA" }}
      >
        <div className="d-flex justify-content-between align-items-center flex-wrap">
          <div className="mb-3 mb-md-0">
            <h2 className="fw-bold" style={{ color: "#6C757D" }}>
              {workspace?.name}
            </h2>
            <p className="text-muted mb-0">
              {workspace?.description || "Không gian làm việc của bạn."}
            </p>
          </div>
          <Button
            className="rounded-pill px-4"
            style={{
              backgroundColor: "#007BFF",
              borderColor: "#007BFF",
            }}
            onClick={() => setShowEditWS(true)}
          >
            Chỉnh sửa
          </Button>
        </div>
      </Card>

      <div className="d-flex justify-content-between align-items-center mb-4">
        <h4 className="fw-bold" style={{ color: "#6C757D" }}>
          Dự án tham gia
        </h4>
        <Button
          style={{ backgroundColor: "#28A745", borderColor: "#28A745" }}
          className="rounded-pill px-4 shadow-sm"
          onClick={() => setShowAddProject(true)}
        >
          Tạo dự án
        </Button>
      </div>

      <Row>
        {projects.length > 0 ? (
          projects.map((p) => (
            <Col key={p.id} lg={4} md={6} className="mb-4">
              <Card
                className="h-100 shadow-sm border-0 rounded-4 overflow-hidden"
                style={{ transition: "all 0.3s ease", cursor: "pointer" }}
                onMouseEnter={(e) =>
                  (e.currentTarget.style.transform = "translateY(-8px)")
                }
                onMouseLeave={(e) =>
                  (e.currentTarget.style.transform = "translateY(0)")
                }
              >
                <Card.Body className="p-4 d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <Link
                      to={`/p/${p.id}`}
                      className="text-decoration-none flex-grow-1"
                    >
                      <Card.Title className="fw-bold text-dark mb-0">
                        {p.name}
                      </Card.Title>
                    </Link>
                    <div className="d-flex align-items-center">
                      <Button
                        variant="light"
                        size="sm"
                        className="shadow-sm me-2"
                        style={{ color: "#FF8C00" }}
                        onClick={(e) => openEditProjectModal(p, e)}
                      >
                        <i className="bi bi-pencil-square"></i> Sửa
                      </Button>
                      <Button
                        variant="light"
                        size="sm"
                        className="shadow-sm"
                        style={{ color: "#FF5733" }}
                        onClick={(e) => handleDeleteProject(p.id, e)}
                      >
                        <i className="bi bi-trash"></i> Xóa
                      </Button>
                    </div>
                  </div>
                  <Card.Text className="text-muted small mb-4 flex-grow-1">
                    {p.description || "Dự án này chưa có mô tả."}
                  </Card.Text>
                  <div className="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
                    <span
                      className="text-muted"
                      style={{ fontSize: "0.75rem" }}
                    >
                      {new Date(p.startAt).toLocaleDateString()} -{" "}
                      {new Date(p.endAt).toLocaleDateString()}
                    </span>
                    <Link
                      to={`/p/${p.id}`}
                      className="fw-bold text-decoration-none small"
                      style={{ color: "#007BFF" }}
                    >
                      Mở
                    </Link>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))
        ) : (
          <Col className="text-center py-5">
            <p className="text-muted">
              Bạn chưa có dự án nào. Hãy tạo dự án đầu tiên!
            </p>
          </Col>
        )}
      </Row>

      <Modal show={showEditWS} onHide={() => setShowEditWS(false)} centered>
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">
            Chỉnh sửa không gian làm việc
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleUpdateWS}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Tên</Form.Label>
              <Form.Control
                type="text"
                value={editWSData.name}
                onChange={(e) =>
                  setEditWSData({ ...editWSData, name: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={editWSData.description}
                onChange={(e) =>
                  setEditWSData({ ...editWSData, description: e.target.value })
                }
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button
              style={{
                backgroundColor: "#6C757D",
                borderColor: "#6C757D",
              }}
              onClick={() => setShowEditWS(false)}
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

      <Modal
        show={showAddProject}
        onHide={() => setShowAddProject(false)}
        centered
        size="md"
      >
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">Tạo dự án mới</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleCreateProject}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Tên</Form.Label>
              <Form.Control
                type="text"
                placeholder="Tên dự án"
                onChange={(e) =>
                  setNewProject({ ...newProject, name: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                onChange={(e) =>
                  setNewProject({ ...newProject, description: e.target.value })
                }
              />
            </Form.Group>
            <Row>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label className="fw-bold small">Bắt đầu</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    onChange={(e) =>
                      setNewProject({ ...newProject, startAt: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label className="fw-bold small">Kết thúc</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    onChange={(e) =>
                      setNewProject({ ...newProject, endAt: e.target.value })
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
              onClick={() => setShowAddProject(false)}
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
              Tạo
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
      <Modal
        show={showEditProject}
        onHide={() => setShowEditProject(false)}
        centered
        size="md"
      >
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">Cập nhật dự án</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleUpdateProject}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Tên</Form.Label>
              <Form.Control
                type="text"
                placeholder="Tên dự án"
                value={editProjectData.name}
                onChange={(e) =>
                  setEditProjectData({
                    ...editProjectData,
                    name: e.target.value,
                  })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={editProjectData.description}
                onChange={(e) =>
                  setEditProjectData({
                    ...editProjectData,
                    description: e.target.value,
                  })
                }
              />
            </Form.Group>
            <Row>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label className="fw-bold small">Bắt đầu</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={editProjectData.startAt}
                    onChange={(e) =>
                      setEditProjectData({
                        ...editProjectData,
                        startAt: e.target.value,
                      })
                    }
                  />
                </Form.Group>
              </Col>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label className="fw-bold small">Kết thúc</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={editProjectData.endAt}
                    onChange={(e) =>
                      setEditProjectData({
                        ...editProjectData,
                        endAt: e.target.value,
                      })
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
              onClick={() => setShowEditProject(false)}
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

export default WorkSpace;
