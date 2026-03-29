import { useEffect, useState, useRef, useMemo, useContext } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Modal,
  Form,
  InputGroup,
  Badge,
} from "react-bootstrap";
import { useParams } from "react-router-dom";
import { authApis, endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/MyContexts";
import MySpinner from "./layout/MySpinner";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import Swal from "sweetalert2";

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
  const [searchEmail, setSearchEmail] = useState("");
  const [searchResults, setSearchResults] = useState([]);

  const [showTaskModal, setShowTaskModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");

  const [showCreateColModal, setShowCreateColModal] = useState(false);
  const [newColName, setNewColName] = useState("");

  const [showCreateTaskModal, setShowCreateTaskModal] = useState(false);
  const [newTaskTitle, setNewTaskTitle] = useState("");
  const [activeColId, setActiveColId] = useState(null);

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
        const sortedCols = boardRes.data.result.sort(
          (a, b) => a.position - b.position,
        );
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

      if (statsRes?.data?.code === "1000") {
        setStats(statsRes.data.result);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải Board:", ex);
    } finally {
      clearTimeout(delayTimer);
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  useEffect(() => {
    if (projectId) loadAllData();
  }, [projectId]);

  const currentUserInProject = members.find(
    (m) => m.userId === currentUser?.id,
  );
  const isSystemAdmin = currentUser?.roles?.some((r) => r.name === "ADMIN");

  const hasManageRights =
    isSystemAdmin ||
    currentUserInProject?.role === "ADMIN" ||
    currentUserInProject?.role === "MANAGER";

  const { totalTasks, completedTasks, completionRate } = useMemo(() => {
    let total = 0;
    let completed = 0;
    columns.forEach((col) => {
      const tasks = col.columnTaskResponses || [];
      total += tasks.length;
      completed += tasks.filter((t) => t.completedAt).length;
    });
    const rate = total === 0 ? 0 : Math.round((completed / total) * 100);
    return {
      totalTasks: total,
      completedTasks: completed,
      completionRate: rate,
    };
  }, [columns]);

  const handleToggleComplete = async (e, task, colId) => {
    e.stopPropagation();
    const isCompleted = e.target.checked;
    const now = isCompleted ? new Date().toISOString() : null;

    const newCols = columns.map((c) => {
      if (c.id === colId) {
        return {
          ...c,
          columnTaskResponses: c.columnTaskResponses.map((t) =>
            t.id === task.id ? { ...t, completedAt: now } : t,
          ),
        };
      }
      return c;
    });
    setColumns(newCols);

    try {
      await authApis().patch(endpoints["update-task"](task.id), {
        completedAt: now,
      });
    } catch (ex) {
      console.error("Lỗi cập nhật task:", ex);
    }
  };

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
          "Chỉ Quản lý hoặc Admin mới được đổi vị trí cột!",
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
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể lưu vị trí công việc", "error");
      loadAllData();
    }
  };

  const handleSearchMember = async () => {
    if (!searchEmail) return;
    try {
      const res = await authApis().get(
        `${endpoints["search-member"](projectId)}?email=${searchEmail}`,
      );
      if (res.data.code === 1000) setSearchResults(res.data.result);
    } catch (ex) {
      Swal.fire("Lỗi", "Không tìm thấy người dùng", "error");
    }
  };

  const handleAddMember = async (userId) => {
    try {
      const res = await authApis().post(endpoints["add-member"](projectId), {
        userId,
        role: "MEMBER",
      });
      if (res.data.code === 1000) {
        Swal.fire({
          icon: "success",
          title: "Đã thêm",
          timer: 1000,
          showConfirmButton: false,
        });
        loadAllData();
      }
    } catch (ex) {
      Swal.fire("Lỗi", "Lỗi thêm thành viên", "error");
    }
  };

  const handleChangeRole = async (userId, newRole) => {
    try {
      await authApis().put(endpoints["change-member-role"](projectId, userId), {
        role: newRole,
      });
      Swal.fire({
        icon: "success",
        title: "Đã đổi quyền",
        timer: 1000,
        showConfirmButton: false,
      });
      loadAllData();
    } catch (ex) {
      Swal.fire("Lỗi", "Lỗi đổi quyền", "error");
    }
  };

  const handleRemoveMember = async (userId) => {
    const result = await Swal.fire({
      title: "Xác nhận?",
      text: "Bạn muốn xóa thành viên này?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Xóa",
    });
    if (result.isConfirmed) {
      try {
        await authApis().delete(endpoints["delete-member"](projectId, userId));
        loadAllData();
      } catch (ex) {
        Swal.fire("Lỗi", "Lỗi xóa thành viên", "error");
      }
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

  const openTaskModalForm = (colId) => {
    setActiveColId(colId);
    setNewTaskTitle("");
    setShowCreateTaskModal(true);
  };

  const submitCreateTask = async (e) => {
    e.preventDefault();
    if (!newTaskTitle) return;
    try {
      const payload = {
        title: newTaskTitle,
        position: 1,
        startAt: new Date().toISOString(),
        dueAt: new Date(Date.now() + 86400000).toISOString(),
        label: "NORMAL",
      };
      await authApis().post(endpoints["create-task"](activeColId), payload);
      setShowCreateTaskModal(false);
      setNewTaskTitle("");
      loadAllData();
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể tạo công việc", "error");
    }
  };

  const openTaskDetail = async (task) => {
    setSelectedTask(task);
    setShowTaskModal(true);
    try {
      const res = await authApis().get(endpoints["get-all-comments"](task.id));
      if (res.data.code === 1000) {
        const cmtData = res.data.result.data || res.data.result;
        setComments(Array.isArray(cmtData) ? cmtData : []);
      }
    } catch (ex) {
      console.error("Lỗi lấy bình luận", ex);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment) return;
    try {
      const res = await authApis().post(
        endpoints["create-comment"](selectedTask.id),
        { content: newComment },
      );
      if (res.data.code === 1000) {
        setComments([res.data.result, ...comments]);
        setNewComment("");
      }
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể gửi bình luận", "error");
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container
      fluid
      className="py-4 px-4"
      style={{ backgroundColor: "#F4F5F7", minHeight: "85vh" }}
    >
      <div className="d-flex justify-content-between align-items-center mb-4 bg-white p-3 rounded shadow-sm">
        <div className="d-flex align-items-center">
          <h5 className="fw-bold text-secondary mb-0 me-4">
            Bảng Kanban - {stats?.projectName || "Đang tải..."}
          </h5>
          <div className="d-flex align-items-center">
            {members.slice(0, 5).map((m) => (
              <div
                key={m.userId}
                className="bg-primary text-white rounded-circle d-flex justify-content-center align-items-center border border-2 border-white me-n2"
                style={{
                  width: "35px",
                  height: "35px",
                  marginLeft: "-10px",
                  fontSize: "0.8rem",
                  zIndex: 1,
                }}
                title={m.email}
              >
                {m.email?.charAt(0).toUpperCase()}
              </div>
            ))}
            {members.length > 5 && (
              <div
                className="bg-secondary text-white rounded-circle d-flex justify-content-center align-items-center border border-2 border-white"
                style={{
                  width: "35px",
                  height: "35px",
                  marginLeft: "-10px",
                  fontSize: "0.8rem",
                  zIndex: 1,
                }}
              >
                +{members.length - 5}
              </div>
            )}
          </div>
        </div>
        <Button
          variant={hasManageRights ? "outline-primary" : "outline-secondary"}
          size="sm"
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
                        className={`bg-light border-0 shadow-sm rounded-3 ${
                          snapshot.isDragging ? "shadow-lg" : ""
                        }`}
                        style={{
                          maxHeight: "70vh",
                          transform: snapshot.isDragging
                            ? "rotate(2deg)"
                            : "none",
                          transition: "transform 0.1s",
                        }}
                      >
                        <Card.Header
                          {...provided.dragHandleProps}
                          className="bg-light border-0 pt-3 pb-2 fw-bold text-secondary d-flex justify-content-between align-items-center"
                          style={{
                            cursor: hasManageRights ? "grab" : "default",
                          }}
                        >
                          {col.name}
                          <Badge bg="secondary" className="rounded-pill">
                            {col.columnTaskResponses?.length || 0}
                          </Badge>
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
                                  ? "#e2e4e6"
                                  : "transparent",
                                transition: "background-color 0.2s ease",
                                minHeight: "50px",
                              }}
                            >
                              {col.columnTaskResponses?.map((task, index) => (
                                <Draggable
                                  key={task.id}
                                  draggableId={task.id}
                                  index={index}
                                >
                                  {(provided, snapshot) => (
                                    <div
                                      ref={provided.innerRef}
                                      {...provided.draggableProps}
                                      {...provided.dragHandleProps}
                                      style={{
                                        ...provided.draggableProps.style,
                                        marginBottom: "8px",
                                      }}
                                    >
                                      <Card
                                        className={`border-0 shadow-sm project-card ${
                                          snapshot.isDragging ? "shadow-lg" : ""
                                        }`}
                                        style={{
                                          cursor: "grab",
                                          transform: snapshot.isDragging
                                            ? "rotate(3deg)"
                                            : "none",
                                          transition: "transform 0.1s",
                                        }}
                                        onClick={() => openTaskDetail(task)}
                                      >
                                        <Card.Body className="p-2">
                                          <div className="d-flex align-items-start">
                                            <Form.Check
                                              type="checkbox"
                                              className="me-2 mt-1"
                                              checked={!!task.completedAt}
                                              onChange={(e) =>
                                                handleToggleComplete(
                                                  e,
                                                  task,
                                                  col.id,
                                                )
                                              }
                                              onClick={(e) =>
                                                e.stopPropagation()
                                              }
                                              title="Đánh dấu hoàn thành"
                                            />
                                            <div
                                              className={`fw-bold small text-dark ${task.completedAt ? "text-decoration-line-through text-muted" : ""}`}
                                            >
                                              {task.title}
                                            </div>
                                          </div>

                                          {task.label && (
                                            <Badge
                                              bg="info"
                                              className="mt-1 ms-4"
                                              style={{ fontSize: "0.6rem" }}
                                            >
                                              {task.label}
                                            </Badge>
                                          )}
                                        </Card.Body>
                                      </Card>
                                    </div>
                                  )}
                                </Draggable>
                              ))}

                              {provided.placeholder}

                              {hasManageRights && (
                                <Button
                                  variant="link"
                                  className="text-muted text-decoration-none small p-0 mt-2 ms-1"
                                  onClick={() => openTaskModalForm(col.id)}
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

      <h5 className="fw-bold text-secondary mb-3">Thống kê dự án</h5>
      <Row>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="text-primary fw-bold">{totalTasks}</h3>
            <span className="text-muted small">Tổng công việc</span>
          </Card>
        </Col>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="text-success fw-bold">{completedTasks}</h3>
            <span className="text-muted small">Đã hoàn thành</span>
          </Card>
        </Col>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="text-warning fw-bold">{completionRate}%</h3>
            <span className="text-muted small">Tỷ lệ hoàn thành</span>
          </Card>
        </Col>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="text-info fw-bold">
              {stats?.totalMembers || members.length}
            </h3>
            <span className="text-muted small">Thành viên tham gia</span>
          </Card>
        </Col>
      </Row>

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
                placeholder="VD: Đang kiểm thử..."
                value={newColName}
                onChange={(e) => setNewColName(e.target.value)}
                autoFocus
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button
              variant="light"
              onClick={() => setShowCreateColModal(false)}
            >
              Hủy
            </Button>
            <Button variant="primary" type="submit">
              Lưu lại
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

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
              <Form.Label className="small fw-bold">
                Tiêu đề công việc
              </Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                placeholder="Nhập nội dung công việc cần làm..."
                value={newTaskTitle}
                onChange={(e) => setNewTaskTitle(e.target.value)}
                autoFocus
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button
              variant="light"
              onClick={() => setShowCreateTaskModal(false)}
            >
              Hủy
            </Button>
            <Button variant="success" type="submit">
              Tạo thẻ
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal
        show={showMemberModal}
        onHide={() => setShowMemberModal(false)}
        size="lg"
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold text-secondary">
            Thành viên dự án
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {hasManageRights && (
            <InputGroup className="mb-4">
              <Form.Control
                placeholder="Nhập email để tìm và mời..."
                value={searchEmail}
                onChange={(e) => setSearchEmail(e.target.value)}
              />
              <Button variant="primary" onClick={handleSearchMember}>
                Tìm kiếm
              </Button>
            </InputGroup>
          )}

          {hasManageRights && searchResults.length > 0 && (
            <div
              className="mb-4 p-3 rounded"
              style={{ backgroundColor: "#eef2f5" }}
            >
              <h6 className="fw-bold text-secondary">Kết quả tìm kiếm:</h6>
              {searchResults.map((user) => (
                <div
                  key={user.userId}
                  className="d-flex justify-content-between align-items-center mb-2"
                >
                  <span>
                    <span className="fw-semibold">{user.email}</span>
                    {user.alreadyMember ? (
                      <span className="text-muted small ms-2">
                        (Đã tham gia)
                      </span>
                    ) : (
                      ""
                    )}
                  </span>
                  {!user.alreadyMember && (
                    <Button
                      size="sm"
                      variant="success"
                      onClick={() => handleAddMember(user.userId)}
                    >
                      Thêm vào dự án
                    </Button>
                  )}
                </div>
              ))}
            </div>
          )}

          <h6 className="fw-bold border-bottom pb-2 mt-4 text-secondary">
            Đang tham gia ({members.length})
          </h6>
          {members.map((m) => (
            <div
              key={m.userId}
              className="d-flex justify-content-between align-items-center py-2 border-bottom"
            >
              <div>
                <span className="fw-semibold">{m.email}</span>
              </div>
              <div className="d-flex align-items-center">
                {hasManageRights ? (
                  <>
                    <Form.Select
                      size="sm"
                      value={m.role}
                      onChange={(e) =>
                        handleChangeRole(m.userId, e.target.value)
                      }
                      style={{ width: "130px", cursor: "pointer" }}
                      className="me-2"
                    >
                      <option value="ADMIN">Admin</option>
                      <option value="MANAGER">Manager</option>
                      <option value="MEMBER">Member</option>
                    </Form.Select>
                    <Button
                      size="sm"
                      variant="outline-danger"
                      onClick={() => handleRemoveMember(m.userId)}
                    >
                      Xóa
                    </Button>
                  </>
                ) : (
                  <Badge
                    bg={
                      m.role === "ADMIN"
                        ? "danger"
                        : m.role === "MANAGER"
                          ? "warning"
                          : "secondary"
                    }
                  >
                    {m.role}
                  </Badge>
                )}
              </div>
            </div>
          ))}
        </Modal.Body>
      </Modal>

      <Modal
        show={showTaskModal}
        onHide={() => setShowTaskModal(false)}
        size="lg"
      >
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold fs-4">
            {selectedTask?.title}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="pt-2">
          <div className="mb-4 text-muted small p-3 bg-light rounded">
            <div className="mb-2">
              <strong>Trạng thái: </strong>
              {selectedTask?.completedAt ? (
                <Badge bg="success">Đã hoàn thành</Badge>
              ) : (
                <Badge bg="warning" text="dark">
                  Đang thực hiện
                </Badge>
              )}
            </div>
            <div className="mb-2">
              <strong>Mô tả:</strong>{" "}
              {selectedTask?.description || (
                <span className="fst-italic text-secondary">
                  Chưa có chi tiết.
                </span>
              )}
            </div>
            <div>
              <strong>Hạn chót:</strong>{" "}
              {selectedTask?.dueAt ? (
                <span className="text-danger fw-bold">
                  {new Date(selectedTask.dueAt).toLocaleString()}
                </span>
              ) : (
                "Chưa thiết lập"
              )}
            </div>
            <div className="mt-2">
              <strong>Nhãn:</strong>{" "}
              {selectedTask?.label && (
                <Badge bg="info">{selectedTask.label}</Badge>
              )}
            </div>
          </div>

          <h6 className="fw-bold mb-3 text-secondary">
            <i className="bi bi-chat-left-text me-2"></i>Hoạt động / Bình luận
          </h6>
          <div
            className="bg-light p-3 rounded mb-3"
            style={{ maxHeight: "350px", overflowY: "auto" }}
          >
            {comments.length > 0 ? (
              comments.map((cmt) => (
                <div
                  key={cmt.id}
                  className="mb-3 bg-white p-3 rounded shadow-sm border-start border-primary border-4"
                >
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <span className="fw-bold text-primary small">
                      User ID: {cmt.userId}
                    </span>
                    <span
                      className="text-muted small"
                      style={{ fontSize: "0.75rem" }}
                    >
                      {new Date(cmt.createdAt).toLocaleString()}{" "}
                      {cmt.isEdited && (
                        <span className="ms-1 fst-italic">(đã sửa)</span>
                      )}
                    </span>
                  </div>
                  <div className="text-dark">{cmt.content}</div>
                </div>
              ))
            ) : (
              <div className="text-muted small text-center py-4 fst-italic">
                Chưa có hoạt động hay bình luận nào.
              </div>
            )}
          </div>

          <Form onSubmit={handleAddComment}>
            <InputGroup>
              <Form.Control
                placeholder="Viết bình luận của bạn..."
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
              />
              <Button type="submit" variant="primary">
                Gửi bình luận
              </Button>
            </InputGroup>
          </Form>
        </Modal.Body>
      </Modal>
    </Container>
  );
};

export default BoardView;
