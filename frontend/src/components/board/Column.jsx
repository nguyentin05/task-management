import { useState } from "react";
import { Card, Badge, Button, Modal, Form, Row, Col } from "react-bootstrap";
import { Droppable, Draggable } from "@hello-pangea/dnd";
import Task, { TASK_COLORS } from "./Task";
import { authApis, endpoints } from "../../configs/Apis";
import Swal from "sweetalert2";

const Column = ({
  col,
  index,
  hasManageRights,
  members,
  currentUser,
  loadAllData,
  handleDeleteColumn,
}) => {
  const [showCreateTaskModal, setShowCreateTaskModal] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState("");
  const [newTaskDescription, setNewTaskDescription] = useState("");
  const [newTaskStartAt, setNewTaskStartAt] = useState("");
  const [newTaskDueAt, setNewTaskDueAt] = useState("");
  const [newTaskLabel, setNewTaskLabel] = useState("");
  const [isEditingName, setIsEditingName] = useState(false);
  const [editName, setEditName] = useState(col.name);

  const colorTranslations = {
    RED: "Đỏ",
    ORANGE: "Cam",
    YELLOW: "Vàng",
    GREEN: "Xanh lá",
    BLUE: "Xanh dương",
    PURPLE: "Tím",
    PINK: "Hồng",
    GRAY: "Xám",
  };

  const handleRenameColumn = async () => {
    if (!editName.trim() || editName === col.name) {
      setIsEditingName(false);
      setEditName(col.name);
      return;
    }

    try {
      await authApis().patch(
        endpoints["change-column-name"](col.projectId, col.id),
        {
          name: editName,
        },
      );
      setIsEditingName(false);
      loadAllData();
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể đổi tên cột", "error");
      setEditName(col.name);
      setIsEditingName(false);
    }
  };

  const openTaskModalForm = () => {
    setNewTaskTitle("");
    setNewTaskDescription("");
    setNewTaskStartAt("");
    setNewTaskDueAt("");
    setNewTaskLabel(Object.keys(TASK_COLORS)[0]);
    setShowCreateTaskModal(true);
  };

  const submitCreateTask = async (e) => {
    e.preventDefault();
    if (!newTaskTitle) return;
    try {
      const payload = {
        title: newTaskTitle,
        description: newTaskDescription,
        position: 0.0,
      };
      if (newTaskStartAt)
        payload.startAt = new Date(newTaskStartAt).toISOString();
      if (newTaskDueAt) payload.dueAt = new Date(newTaskDueAt).toISOString();
      if (newTaskLabel) payload.label = newTaskLabel;

      await authApis().post(endpoints["create-task"](col.id), payload);
      setShowCreateTaskModal(false);
      loadAllData();
    } catch (ex) {
      Swal.fire(
        "Lỗi",
        ex.response?.data?.message || "Không thể tạo công việc",
        "error",
      );
    }
  };

  return (
    <>
      <Draggable
        key={col.id}
        draggableId={col.id}
        index={index}
        isDragDisabled={!hasManageRights}
      >
        {(provided, snapshot) => (
          <div
            ref={provided.innerRef}
            {...provided.draggableProps}
            className="flex-shrink-0 me-3"
            style={{
              width: "300px",
              ...provided.draggableProps.style,
              opacity: snapshot.isDragging ? 0.9 : 1,
            }}
          >
            <Card
              className={`bg-light border-0 shadow-sm rounded-3 ${snapshot.isDragging ? "shadow-lg" : ""}`}
              style={{
                maxHeight: "70vh",
                transform: snapshot.isDragging ? "rotate(2deg)" : "none",
                transition: "transform 0.1s",
              }}
            >
              <Card.Header
                {...provided.dragHandleProps}
                className="bg-light border-0 pt-3 pb-2 fw-bold text-secondary d-flex justify-content-between align-items-center"
                style={{ cursor: hasManageRights ? "grab" : "default" }}
              >
                <div className="flex-grow-1 me-2 overflow-hidden">
                  {isEditingName ? (
                    <Form.Control
                      type="text"
                      value={editName}
                      onChange={(e) => setEditName(e.target.value)}
                      onBlur={handleRenameColumn}
                      onKeyDown={(e) =>
                        e.key === "Enter" && handleRenameColumn()
                      }
                      autoFocus
                      size="sm"
                      className="fw-bold shadow-none border-primary p-1 m-0 h-auto"
                    />
                  ) : (
                    <span
                      className="d-block w-100 p-1 text-truncate"
                      style={{
                        cursor: hasManageRights ? "pointer" : "default",
                      }}
                      onDoubleClick={(e) => {
                        e.stopPropagation();
                        if (hasManageRights) setIsEditingName(true);
                      }}
                      title={col.name}
                    >
                      {col.name}
                    </span>
                  )}
                </div>
                <div className="d-flex align-items-center flex-shrink-0">
                  <Badge bg="secondary" className="rounded-pill">
                    {col.columnTaskResponses?.length || 0}
                  </Badge>
                  {hasManageRights && (
                    <Button
                      variant="light"
                      size="sm"
                      className="ms-2 shadow-sm py-0 px-2"
                      style={{
                        borderColor: "#FF5733",
                        color: "#FF5733",
                      }}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDeleteColumn(col.id);
                      }}
                    >
                      <i className="bi bi-trash me-1"></i> Xóa
                    </Button>
                  )}
                </div>
              </Card.Header>

              <Droppable droppableId={col.id} type="task">
                {(provided, snapshot) => (
                  <Card.Body
                    ref={provided.innerRef}
                    {...provided.droppableProps}
                    className="overflow-auto pt-0"
                    style={{
                      maxHeight: "calc(70vh - 100px)",
                      backgroundColor: snapshot.isDraggingOver
                        ? "#E2E4E6"
                        : "transparent",
                      transition: "background-color 0.2s ease",
                      minHeight: "50px",
                    }}
                  >
                    {col.columnTaskResponses?.map((task, idx) => (
                      <Task
                        key={task.id}
                        task={task}
                        index={idx}
                        colId={col.id}
                        hasManageRights={hasManageRights}
                        members={members}
                        currentUser={currentUser}
                        loadAllData={loadAllData}
                      />
                    ))}
                    {provided.placeholder}

                    {hasManageRights && (
                      <Button
                        variant="link"
                        className="text-muted text-decoration-none small p-0 mt-2 ms-1"
                        onClick={openTaskModalForm}
                      >
                        + Thêm thẻ mới
                      </Button>
                    )}
                  </Card.Body>
                )}
              </Droppable>
            </Card>
          </div>
        )}
      </Draggable>

      <Modal
        show={showCreateTaskModal}
        onHide={() => setShowCreateTaskModal(false)}
        centered
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold fs-5">Thêm thẻ công việc</Modal.Title>
        </Modal.Header>
        <Form onSubmit={submitCreateTask}>
          <Modal.Body>
            <Form.Group>
              <Form.Label className="small fw-bold">Tiêu đề</Form.Label>
              <Form.Control
                type="text"
                value={newTaskTitle}
                onChange={(e) => setNewTaskTitle(e.target.value)}
                autoFocus
              />
            </Form.Group>
            <Form.Group className="mt-3">
              <Form.Label className="small fw-bold">Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={newTaskDescription}
                onChange={(e) => setNewTaskDescription(e.target.value)}
              />
            </Form.Group>
            <Row className="mt-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label className="small fw-bold">Bắt đầu</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={newTaskStartAt}
                    onChange={(e) => setNewTaskStartAt(e.target.value)}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label className="small fw-bold">Hạn chót</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    value={newTaskDueAt}
                    onChange={(e) => setNewTaskDueAt(e.target.value)}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mt-3">
              <Form.Label className="small fw-bold">Nhãn màu</Form.Label>
              <Form.Select
                value={newTaskLabel}
                onChange={(e) => setNewTaskLabel(e.target.value)}
                style={{ fontWeight: "bold" }}
              >
                {Object.keys(TASK_COLORS).map((key) => (
                  <option key={key} value={key}>
                    {colorTranslations[key] || key}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button
              style={{ backgroundColor: "#6C757D", borderColor: "#6C757D" }}
              onClick={() => setShowCreateTaskModal(false)}
            >
              Hủy
            </Button>
            <Button
              type="submit"
              style={{ backgroundColor: "#28A745", borderColor: "#28A745" }}
            >
              Tạo
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </>
  );
};

export default Column;
