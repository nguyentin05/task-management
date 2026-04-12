import { useEffect, useState, useRef, useContext } from "react";
import { Container, Button, Modal, Form } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { authApis, endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/MyContexts";
import MySpinner from "./layout/MySpinner";
import { DragDropContext, Droppable } from "@hello-pangea/dnd";
import Swal from "sweetalert2";
import Column from "./board/Column";
import Statistic from "./board/Statistic";
import Member from "./board/Member";

const BoardView = ({ projectId: propProjectId }) => {
  const { projectId: paramProjectId } = useParams();
  const projectId = propProjectId || paramProjectId;
  const [currentUser] = useContext(MyUserContext);

  const [loading, setLoading] = useState(true);
  const loadingStartTime = useRef(null);

  const [members, setMembers] = useState([]);
  const [columns, setColumns] = useState([]);
  const [stats, setStats] = useState(null);

  const [showMemberModal, setShowMemberModal] = useState(false);
  const [showCreateColModal, setShowCreateColModal] = useState(false);
  const [newColName, setNewColName] = useState("");

  const ensureSpinnerMinTime = () => {
    if (!loadingStartTime.current) return Promise.resolve();
    const displayTime = Date.now() - loadingStartTime.current;
    const minDisplay = 500;
    if (displayTime < minDisplay)
      return new Promise((r) => setTimeout(r, minDisplay - displayTime));
    return Promise.resolve();
  };

  const loadAllData = async () => {
    const delayTimer = setTimeout(() => {
      setLoading(true);
      loadingStartTime.current = Date.now();
    }, 300);

    try {
      const api = authApis();
      const [boardRes, memberRes, statsRes] = await Promise.all([
        api.get(endpoints["get-kanban-board"](projectId)),
        api.get(endpoints["get-all-member"](projectId)),
        api.get(endpoints["statistics"](projectId)).catch(() => null),
      ]);

      await ensureSpinnerMinTime();

      if (boardRes.data.code === 1000) {
        const sortedCols = boardRes.data.result.data;
        sortedCols.forEach((col) => {
          if (col.columnTaskResponses) {
            col.columnTaskResponses.sort((a, b) => a.position - b.position);
          }
        });
        setColumns(sortedCols);
      }

      if (memberRes.data.code === 1000) {
        const memData = memberRes.data.result.data || memberRes.data.result;
        setMembers(Array.isArray(memData) ? memData : []);
      }

      if (statsRes?.data?.code === 1000) {
        setStats(statsRes.data.result);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải bảng:", ex);
    } finally {
      clearTimeout(delayTimer);
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  const reloadStats = async () => {
    try {
      const statsRes = await authApis().get(endpoints["statistics"](projectId));
      if (statsRes?.data?.code === 1000) {
        setStats(statsRes.data.result);
      }
    } catch (ex) {
      console.error("Lỗi tải lại thống kê:", ex);
    }
  };

  useEffect(() => {
    if (projectId) loadAllData();
  }, [projectId]);

  const currentUserInProject = members.find((m) => m.userId == currentUser?.id);
  const isSystemAdmin = currentUser?.roles?.some((r) => r.name === "ADMIN");

  const hasManageRights =
    isSystemAdmin ||
    currentUserInProject?.role === "ADMIN" ||
    currentUserInProject?.role === "MANAGER";

  const handleDragEnd = async (result) => {
    const { destination, source, draggableId, type } = result;

    if (!destination) return;
    if (
      destination.droppableId === source.droppableId &&
      destination.index === source.index
    )
      return;

    const newColumns = JSON.parse(JSON.stringify(columns));

    if (type === "column") {
      if (!hasManageRights) {
        Swal.fire(
          "Từ chối",
          "Chỉ quản lý hoặc Admin mới được đổi vị trí cột!",
          "warning",
        );
        return;
      }

      const [movedCol] = newColumns.splice(source.index, 1);

      let newPosition = 1000;
      if (newColumns.length === 0) newPosition = 1000;
      else if (destination.index === 0)
        newPosition = newColumns[0].position / 2;
      else if (destination.index >= newColumns.length)
        newPosition = newColumns[newColumns.length - 1].position + 1000;
      else {
        const prevPos = newColumns[destination.index - 1].position;
        const nextPos = newColumns[destination.index].position;
        newPosition = (prevPos + nextPos) / 2;
      }

      movedCol.position = newPosition;
      newColumns.splice(destination.index, 0, movedCol);
      newColumns.sort((a, b) => a.position - b.position);

      setColumns(newColumns);

      try {
        await authApis().patch(
          endpoints["change-column-name"](projectId, draggableId),
          { position: newPosition },
        );
      } catch (ex) {
        Swal.fire("Lỗi", "Không thể lưu vị trí cột", "error");
        loadAllData();
      }
      return;
    }

    const sourceCol = newColumns.find((c) => c.id === source.droppableId);
    const destCol = newColumns.find((c) => c.id === destination.droppableId);

    const [movedTask] = sourceCol.columnTaskResponses.splice(source.index, 1);

    const destTasks = destCol.columnTaskResponses || [];
    let newPosition = 1000;

    if (destTasks.length === 0) newPosition = 1000;
    else if (destination.index === 0) newPosition = destTasks[0].position / 2;
    else if (destination.index >= destTasks.length)
      newPosition = destTasks[destTasks.length - 1].position + 1000;
    else {
      const prevPos = destTasks[destination.index - 1].position;
      const nextPos = destTasks[destination.index].position;
      newPosition = (prevPos + nextPos) / 2;
    }

    movedTask.position = newPosition;
    movedTask.columnId = destination.droppableId;

    destCol.columnTaskResponses = destCol.columnTaskResponses || [];
    destCol.columnTaskResponses.splice(destination.index, 0, movedTask);
    destCol.columnTaskResponses.sort((a, b) => a.position - b.position);

    setColumns(newColumns);

    try {
      await authApis().put(endpoints["move-task"](draggableId), {
        columnId: destination.droppableId,
        position: newPosition,
      });
      reloadStats();
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể lưu vị trí công việc", "error");
      loadAllData();
    }
  };

  const submitCreateColumn = async (e) => {
    e.preventDefault();
    if (!newColName) return;
    try {
      await authApis().post(endpoints["create-column"](projectId), {
        name: newColName,
        position: columns.length + 1,
      });
      setShowCreateColModal(false);
      setNewColName("");
      loadAllData();
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể tạo cột", "error");
    }
  };

  const handleDeleteColumn = async (columnId) => {
    const result = await Swal.fire({
      title: "Xác nhận?",
      text: "Bạn có chắc chắn muốn xóa cột này không?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#FF5733",
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
      cancelButtonColor: "#6C757D",
    });

    if (result.isConfirmed) {
      try {
        await authApis().delete(
          endpoints["delete-column"](projectId, columnId),
        );
        setColumns((prevCols) => prevCols.filter((c) => c.id !== columnId));
        Swal.fire("Đã xóa", "Cột đã được xóa khỏi bảng.", "success");
      } catch (ex) {
        Swal.fire("Lỗi", "Không thể xóa cột!", "error");
      }
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container
      fluid
      className="py-4 px-4"
      style={{ backgroundColor: "#F4F5F7", minHeight: "85vh" }}
    >
      <div className="d-flex justify-content-end align-items-center mb-4 bg-white p-3 rounded shadow-sm">
        <Button
          size="sm"
          style={{
            backgroundColor: "#007BFF",
            borderColor: "#007BFF",
            color: "#FFF",
          }}
          onClick={() => setShowMemberModal(true)}
        >
          {hasManageRights ? "Quản lý thành viên" : "Xem thành viên"}
        </Button>
      </div>

      <DragDropContext onDragEnd={handleDragEnd}>
        <Droppable
          droppableId="board-columns"
          direction="horizontal"
          type="column"
        >
          {(provided) => (
            <div
              ref={provided.innerRef}
              {...provided.droppableProps}
              className="d-flex align-items-start mb-5 pb-3"
              style={{ overflowX: "auto", minHeight: "50vh" }}
            >
              {columns.map((col, index) => (
                <Column
                  key={col.id}
                  col={col}
                  index={index}
                  members={members}
                  hasManageRights={hasManageRights}
                  currentUser={currentUser}
                  loadAllData={loadAllData}
                  handleDeleteColumn={handleDeleteColumn}
                />
              ))}

              {provided.placeholder}

              {hasManageRights && (
                <Button
                  variant="outline-secondary"
                  className="flex-shrink-0 bg-white shadow-sm border-0"
                  style={{ width: "300px", textAlign: "left" }}
                  onClick={() => {
                    setNewColName("");
                    setShowCreateColModal(true);
                  }}
                >
                  + Thêm cột khác
                </Button>
              )}
            </div>
          )}
        </Droppable>
      </DragDropContext>

      <Statistic columns={columns} stats={stats} members={members} />

      <Member
        show={showMemberModal}
        onHide={() => setShowMemberModal(false)}
        members={members}
        hasManageRights={hasManageRights}
        projectId={projectId}
        loadAllData={loadAllData}
      />

      <Modal
        show={showCreateColModal}
        onHide={() => setShowCreateColModal(false)}
        centered
        size="sm"
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold fs-5">Tạo cột mới</Modal.Title>
        </Modal.Header>
        <Form onSubmit={submitCreateColumn}>
          <Modal.Body>
            <Form.Group>
              <Form.Label className="small fw-bold">Tên cột</Form.Label>
              <Form.Control
                type="text"
                placeholder="Nhập tên cột"
                value={newColName}
                onChange={(e) => setNewColName(e.target.value)}
                autoFocus
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button
              style={{
                backgroundColor: "#6C757D",
                borderColor: "#6C757D",
              }}
              onClick={() => setShowCreateColModal(false)}
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

export default BoardView;
