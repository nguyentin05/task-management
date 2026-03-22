import { Draggable } from "@hello-pangea/dnd";
import { useState } from "react";
import { Button, Modal, Form } from "react-bootstrap";
import { authApis } from "../../configs/Apis";

const TaskManager = ({ task, index, reloadColumns }) => {
  const [showEdit, setShowEdit] = useState(false);
  const [title, setTitle] = useState(task.title);

  const updateTask = async () => {
    try {
      await authApis().patch(`/tasks/${task.id}`, { title });
      setShowEdit(false);
      reloadColumns();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteTask = async () => {
    if (!window.confirm("Xóa task?")) return;
    try {
      await authApis().delete(`/tasks/${task.id}`);
      reloadColumns();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <Draggable draggableId={task.id} index={index}>
      {(provided) => (
        <div
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          ref={provided.innerRef}
          style={{
            border: "1px solid #aaa",
            borderRadius: "5px",
            padding: "8px",
            marginBottom: "5px",
            background: "white",
            ...provided.draggableProps.style,
          }}
        >
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <span>{task.title}</span>
            <div>
              <Button size="sm" onClick={() => setShowEdit(true)}>
                Sửa
              </Button>
              <Button size="sm" variant="danger" onClick={deleteTask}>
                Xóa
              </Button>
            </div>
          </div>

          <Modal show={showEdit} onHide={() => setShowEdit(false)}>
            <Modal.Header closeButton>
              <Modal.Title>Sửa Task</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form.Control
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </Modal.Body>
            <Modal.Footer>
              <Button variant="danger" onClick={() => setShowEdit(false)}>
                Hủy
              </Button>
              <Button variant="primary" onClick={updateTask}>
                Lưu
              </Button>
            </Modal.Footer>
          </Modal>
        </div>
      )}
    </Draggable>
  );
};

export default TaskManager;
