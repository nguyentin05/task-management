import { useState, useEffect } from "react";
import { Card, Form, Badge, Modal, Row, Col, Button } from "react-bootstrap";
import { Draggable } from "@hello-pangea/dnd";
import { authApis, endpoints } from "../../configs/Apis";
import Swal from "sweetalert2";
import Comment from "./Comment";

export const TASK_COLORS = {
  RED: "#FF5733",
  ORANGE: "#FF8C00",
  YELLOW: "#FFD700",
  GREEN: "#28A745",
  BLUE: "#007BFF",
  PURPLE: "#6F42C1",
  PINK: "#E83E8C",
  GRAY: "#6C757D",
};

const COLOR_TRANSLATIONS = {
  RED: "Đỏ",
  ORANGE: "Cam",
  YELLOW: "Vàng",
  GREEN: "Xanh lá",
  BLUE: "Xanh dương",
  PURPLE: "Tím",
  PINK: "Hồng",
  GRAY: "Xám",
};

const Task = ({
  task,
  index,
  hasManageRights,
  members,
  currentUser,
  loadAllData,
}) => {
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [editTaskData, setEditTaskData] = useState({});

  useEffect(() => {
    setEditTaskData((prev) => ({
      ...prev,
      title: task.title || "",
      description: task.description || "",
      dueAt: task.dueAt ? task.dueAt.substring(0, 16) : "",
      label: task.label
        ? String(task.label).replace(/\s/g, "").toUpperCase()
        : Object.keys(TASK_COLORS)[0],
      assigneeId: prev.assigneeId || task.assigneeId || "",
    }));
  }, [task]);

  const rawLabel = task.label;
  const cleanLabel = rawLabel
    ? String(rawLabel).replace(/\s/g, "").toUpperCase()
    : "";
  const bgColor = TASK_COLORS[cleanLabel] || "#FFF";
  const isColored = !!TASK_COLORS[cleanLabel];
  const textColor = isColored ? "#FFF" : "#212529";

  const openTaskDetail = async () => {
    try {
      const res = await authApis().get(endpoints["get-task"](task.id));

      if (res.data.code === 1000) {
        const fullTask = res.data.result;
        setEditTaskData({
          title: fullTask.title || "",
          description: fullTask.description || "",
          dueAt: fullTask.dueAt ? fullTask.dueAt.substring(0, 16) : "",
          label: cleanLabel || Object.keys(TASK_COLORS)[0],
          assigneeId: fullTask.assigneeId || "",
        });
        setShowTaskModal(true);
      }
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể tải chi tiết công việc", "error");
    }
  };

  const handleUpdateTask = async () => {
    try {
      const payload = {
        title: editTaskData.title,
        description: editTaskData.description,
        label: editTaskData.label || null,
      };
      if (editTaskData.dueAt)
        payload.dueAt = new Date(editTaskData.dueAt).toISOString();
      await authApis().patch(endpoints["update-task"](task.id), payload);
      Swal.fire({
        icon: "success",
        title: "Đã cập nhật",
        timer: 1000,
        showConfirmButton: false,
      });
      setShowTaskModal(false);
      loadAllData();
    } catch (error) {
      Swal.fire("Lỗi", "Không thể cập nhật công việc", "error");
    }
  };

  const handleDeleteTask = async () => {
    const result = await Swal.fire({
      title: "Bạn có chắc chắn muốn xóa công việc này không?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#FF5733",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
      cancelButtonColor: "#6C757D",
    });
    if (result.isConfirmed) {
      try {
        await authApis().delete(endpoints["delete-task"](task.id));
        setShowTaskModal(false);
        loadAllData();
      } catch (error) {
        Swal.fire("Lỗi", "Không thể xóa công việc", "error");
      }
    }
  };

  const handleAssignUser = async (userId) => {
    setEditTaskData((prev) => ({ ...prev, assigneeId: userId }));
    try {
      if (userId)
        await authApis().post(endpoints["assignees"](task.id), { userId });
      loadAllData();
    } catch (error) {
      Swal.fire("Lỗi", "Lỗi gán thành viên", "error");
    }
  };

  return (
    <>
      <Draggable key={task.id} draggableId={task.id} index={index}>
        {(provided, snapshot) => (
          <div
            ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}
            style={{ ...provided.draggableProps.style, marginBottom: "8px" }}
          >
            <Card
              className={`border-0 shadow-sm project-card ${snapshot.isDragging ? "shadow-lg" : ""}`}
              style={{
                cursor: "grab",
                transform: snapshot.isDragging ? "rotate(3deg)" : "none",
                transition: "transform 0.1s",
                backgroundColor: bgColor,
                color: textColor,
                border: isColored
                  ? `1px solid ${bgColor}`
                  : "1px solid #DEE2E6",
              }}
              onClick={openTaskDetail}
            >
              <Card.Body className="p-2">
                <div className="d-flex align-items-start">
                  <div className={"fw-bold small"}>{task.title}</div>
                </div>
              </Card.Body>
            </Card>
          </div>
        )}
      </Draggable>

      <Modal
        show={showTaskModal}
        onHide={() => setShowTaskModal(false)}
        size="lg"
        onClick={(e) => e.stopPropagation()}
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold fs-4">{task.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body className="pt-2">
          <div className="mb-4 bg-light p-3 rounded border">
            {hasManageRights ? (
              <Form>
                <Row>
                  <Col md={8}>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-bold small">Tiêu đề</Form.Label>
                      <Form.Control
                        type="text"
                        value={editTaskData.title || ""}
                        onChange={(e) =>
                          setEditTaskData({
                            ...editTaskData,
                            title: e.target.value,
                          })
                        }
                      />
                    </Form.Group>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-bold small">Mô tả</Form.Label>
                      <Form.Control
                        as="textarea"
                        rows={3}
                        value={editTaskData.description || ""}
                        onChange={(e) =>
                          setEditTaskData({
                            ...editTaskData,
                            description: e.target.value,
                          })
                        }
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-bold small">
                        Hạn chót
                      </Form.Label>
                      <Form.Control
                        type="datetime-local"
                        value={editTaskData.dueAt || ""}
                        onChange={(e) =>
                          setEditTaskData({
                            ...editTaskData,
                            dueAt: e.target.value,
                          })
                        }
                      />
                    </Form.Group>
                    <Form.Group className="mb-3">
                      <Form.Label className="fw-bold small">
                        Nhãn màu
                      </Form.Label>
                      <Form.Select
                        value={editTaskData.label || ""}
                        onChange={(e) =>
                          setEditTaskData({
                            ...editTaskData,
                            label: e.target.value,
                          })
                        }
                        style={{
                          fontWeight: "bold",
                        }}
                      >
                        {Object.keys(TASK_COLORS).map((key) => (
                          <option key={key} value={key}>
                            {COLOR_TRANSLATIONS[key] || key}
                          </option>
                        ))}
                      </Form.Select>
                    </Form.Group>
                    <Form.Group className="mb-3 border-top pt-2">
                      <Form.Label className="fw-bold small">
                        Người thực hiện
                      </Form.Label>
                      <Form.Select
                        value={editTaskData.assigneeId || ""}
                        onChange={(e) => handleAssignUser(e.target.value)}
                      >
                        {members.map((m) => (
                          <option key={m.userId} value={m.userId}>
                            {m.email || "Chưa lấy được email"}
                          </option>
                        ))}
                      </Form.Select>
                    </Form.Group>
                  </Col>
                </Row>
                <div className="d-flex justify-content-end gap-2 mt-2">
                  <Button
                    size="sm"
                    style={{
                      backgroundColor: "#FF5733",
                      borderColor: "#FF5733",
                    }}
                    onClick={handleDeleteTask}
                  >
                    Xóa Task
                  </Button>
                  <Button
                    size="sm"
                    style={{
                      backgroundColor: "#28A745",
                      borderColor: "#28A745",
                    }}
                    onClick={handleUpdateTask}
                  >
                    Lưu thay đổi
                  </Button>
                </div>
              </Form>
            ) : (
              <>
                <div className="mb-2">
                  <strong>Giao cho: </strong>
                  {(() => {
                    const assignedUser = members.find(
                      (m) => m.userId == editTaskData.assigneeId,
                    );

                    if (assignedUser) {
                      return (
                        <span>
                          {assignedUser.email || "Chưa lấy được email"}
                        </span>
                      );
                    }

                    return (
                      <span className="fst-italic text-secondary">
                        Chưa giao cho ai
                      </span>
                    );
                  })()}
                </div>
                <div className="mb-2">
                  <strong>Mô tả:</strong>{" "}
                  {editTaskData.description || (
                    <span className="fst-italic text-secondary">
                      Chưa có chi tiết.
                    </span>
                  )}
                </div>
                <div className="mb-2">
                  <strong>Hạn chót:</strong>{" "}
                  {editTaskData.dueAt ? (
                    <span className="text-danger fw-bold">
                      {new Date(editTaskData.dueAt).toLocaleString()}
                    </span>
                  ) : (
                    "Chưa thiết lập"
                  )}
                </div>
              </>
            )}
          </div>

          {showTaskModal && (
            <Comment
              taskId={task.id}
              assigneeId={editTaskData.assigneeId}
              currentUserId={currentUser?.id}
              hasManageRights={hasManageRights}
              members={members}
            />
          )}
        </Modal.Body>
      </Modal>
    </>
  );
};

export default Task;
