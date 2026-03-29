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
} from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import { Link } from "react-router-dom";
import MySpinner from "../layout/MySpinner";
import Swal from "sweetalert2";

const Workspaces = () => {
  const [loading, setLoading] = useState(true);
  const loadingStartTime = useRef(null);
  const [workspaces, setWorkspaces] = useState([]);

  const [showEdit, setShowEdit] = useState(false);
  const [selectedWs, setSelectedWs] = useState(null);
  const [editData, setEditData] = useState({ name: "", description: "" });

  const ensureSpinnerMinTime = () => {
    if (!loadingStartTime.current) return Promise.resolve();
    const displayTime = Date.now() - loadingStartTime.current;
    if (displayTime < 500)
      return new Promise((r) => setTimeout(r, 500 - displayTime));
    return Promise.resolve();
  };

  const loadData = async () => {
    const delayTimer = setTimeout(() => {
      setLoading(true);
      loadingStartTime.current = Date.now();
    }, 300);

    try {
      const res = await authApis().get(
        `${endpoints["get-all-workspaces"]}?size=100`,
      );

      await ensureSpinnerMinTime();

      if (res.data.code === 1000) {
        const wsData = res.data.result.data || res.data.result;
        setWorkspaces(Array.isArray(wsData) ? wsData : []);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải Workspace:", ex);
      Swal.fire("Lỗi", "Không thể tải dữ liệu không gian làm việc", "error");
    } finally {
      clearTimeout(delayTimer);
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openEditModal = (ws) => {
    setSelectedWs(ws);
    setEditData({ name: ws.name || "", description: ws.description || "" });
    setShowEdit(true);
  };

  const handleUpdateWorkspace = async (e) => {
    e.preventDefault();
    try {
      const res = await authApis().patch(
        endpoints["update-workspace"](selectedWs.id),
        editData,
      );
      if (res.data.code === 1000) {
        Swal.fire({
          icon: "success",
          title: "Thành công",
          text: "Đã cập nhật Workspace!",
          timer: 1500,
        });
        setShowEdit(false);
        setWorkspaces(
          workspaces.map((w) =>
            w.id === selectedWs.id ? { ...w, ...editData } : w,
          ),
        );
      }
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể cập nhật thông tin Workspace!", "error");
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container
      fluid
      className="py-4 px-lg-5"
      style={{ minHeight: "85vh", backgroundColor: "#f8f9fa" }}
    >
      <div className="mb-5">
        <h3 className="fw-bold mb-4" style={{ color: "#6C757D" }}>
          Quản lý Không gian làm việc
        </h3>
        <Row>
          <Col md={6} lg={4} className="mb-3">
            <Card className="border-0 shadow-sm rounded-4 py-4 px-4 d-flex flex-row align-items-center">
              <div
                className="bg-primary text-white rounded-circle d-flex justify-content-center align-items-center me-4"
                style={{ width: "60px", height: "60px", fontSize: "1.5rem" }}
              >
                <i className="bi bi-building"></i>
              </div>
              <div>
                <h2 className="text-primary fw-bold mb-0">
                  {workspaces.length}
                </h2>
                <span className="text-muted fw-semibold small">
                  Tổng số Không gian
                </span>
              </div>
            </Card>
          </Col>
        </Row>
      </div>

      <Card className="border-0 shadow-sm rounded-4 overflow-hidden">
        <Table hover responsive className="mb-0 align-middle">
          <thead className="bg-light text-muted">
            <tr>
              <th className="py-3 px-4 border-0">Workspace (Không gian)</th>
              <th className="py-3 border-0">Mô tả chi tiết</th>
              <th className="py-3 border-0 text-end px-4">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {workspaces.map((ws) => (
              <tr key={ws.id}>
                <td className="px-4 py-3">
                  <div className="fw-bold text-dark fs-6">{ws.name}</div>
                  <div className="text-muted small">
                    ID: {ws.id.substring(0, 8)}...
                  </div>
                </td>
                <td style={{ maxWidth: "400px" }}>
                  <div
                    className="text-truncate text-muted small"
                    title={ws.description}
                  >
                    {ws.description || (
                      <span className="fst-italic">Không có mô tả</span>
                    )}
                  </div>
                </td>
                <td className="text-end px-4">
                  <Button
                    variant="light"
                    size="sm"
                    className="me-3 text-primary shadow-sm"
                    onClick={() => openEditModal(ws)}
                    title="Chỉnh sửa"
                  >
                    <i className="bi bi-pencil-square"></i> Sửa
                  </Button>

                  <Button
                    as={Link}
                    to={`/admin/workspaces/${ws.id}/projects`}
                    variant="primary"
                    size="sm"
                    className="shadow-sm rounded-pill px-3"
                  >
                    Quản lý Dự án →
                  </Button>
                </td>
              </tr>
            ))}
            {workspaces.length === 0 && (
              <tr>
                <td colSpan="3" className="text-center py-5 text-muted">
                  Hệ thống chưa có Workspace nào.
                </td>
              </tr>
            )}
          </tbody>
        </Table>
      </Card>

      <Modal show={showEdit} onHide={() => setShowEdit(false)} centered>
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">Chỉnh sửa Không gian</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleUpdateWorkspace}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Tên Workspace</Form.Label>
              <Form.Control
                type="text"
                value={editData.name}
                onChange={(e) =>
                  setEditData({ ...editData, name: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="fw-bold small">Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={4}
                value={editData.description}
                onChange={(e) =>
                  setEditData({ ...editData, description: e.target.value })
                }
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button variant="light" onClick={() => setShowEdit(false)}>
              Hủy
            </Button>
            <Button variant="success" type="submit">
              Lưu thay đổi
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Workspaces;
