import { useState } from "react";
import { Draggable, Droppable } from "@hello-pangea/dnd";
import TaskManager from "./TaskManager";
import { authApis } from "../../configs/Apis";
import { Button, Modal, Form } from "react-bootstrap";

const ColumnManager = ({ column, index, reloadColumns }) => {
  const [showEdit, setShowEdit] = useState(false);
  const [name, setName] = useState(column.name);

  const updateColumn = async () => {
    try {
      await authApis().patch(
        `/projects/${column.projectId}/columns/${column.id}`,
        {
          name,
          position: index,
        },
      );
      setShowEdit(false);
      reloadColumns();
    } catch (err) {
      console.error(err);
    }
  };

  const deleteColumn = async () => {
    if (!window.confirm("Xóa cột?")) return;
    try {
      await authApis().delete(
        `/projects/${column.projectId}/columns/${column.id}`,
      );
      reloadColumns();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <Draggable draggableId={column.id} index={index}>
      {(provided) => (
        <div
          {...provided.draggableProps}
          ref={provided.innerRef}
          style={{
            border: "1px solid #ccc",
            borderRadius: "5px",
            padding: "10px",
            minWidth: "250px",
            background: "#f7f7f7",
            ...provided.draggableProps.style,
          }}
        >
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <h5 {...provided.dragHandleProps}>{column.name}</h5>
            <div>
              <Button size="sm" onClick={() => setShowEdit(true)}>
                Sửa
              </Button>
              <Button size="sm" variant="danger" onClick={deleteColumn}>
                Xóa
              </Button>
            </div>
          </div>

          <Droppable droppableId={column.id} type="TASK">
            {(provided) => (
              <div
                ref={provided.innerRef}
                {...provided.droppableProps}
                style={{ minHeight: "50px" }}
              >
                {column.tasks.map((task, idx) => (
                  <TaskManager
                    key={task.id}
                    task={task}
                    index={idx}
                    reloadColumns={reloadColumns}
                  />
                ))}
                {provided.placeholder}
              </div>
            )}
          </Droppable>

          <Modal show={showEdit} onHide={() => setShowEdit(false)}>
            <Modal.Header closeButton>
              <Modal.Title>Sửa tên cột</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form.Control
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </Modal.Body>
            <Modal.Footer>
              <Button variant="danger" onClick={() => setShowEdit(false)}>
                Hủy
              </Button>
              <Button variant="primary" onClick={updateColumn}>
                Lưu
              </Button>
            </Modal.Footer>
          </Modal>
        </div>
      )}
    </Draggable>
  );
};

export default ColumnManager;
