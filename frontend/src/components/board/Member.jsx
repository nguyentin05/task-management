import { useState, useEffect } from "react";
import { Modal, InputGroup, Form, Button } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import Swal from "sweetalert2";

const Member = ({
  show,
  onHide,
  members,
  hasManageRights,
  projectId,
  loadAllData,
}) => {
  const [searchEmail, setSearchEmail] = useState("");
  const [searchResults, setSearchResults] = useState([]);

  useEffect(() => {
    if (!show) {
      setSearchEmail("");
      setSearchResults([]);
    }
  }, [show]);

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

        setSearchResults((prev) =>
          prev.map((user) =>
            user.userId === userId ? { ...user, alreadyMember: true } : user,
          ),
        );
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
      confirmButtonColor: "#FF5733",
      confirmButtonText: "Xóa",
    });
    if (result.isConfirmed) {
      try {
        await authApis().delete(endpoints["delete-member"](projectId, userId));
        loadAllData();

        setSearchResults((prev) =>
          prev.map((user) =>
            user.userId === userId ? { ...user, alreadyMember: false } : user,
          ),
        );
      } catch (ex) {
        Swal.fire("Lỗi", "Lỗi xóa thành viên", "error");
      }
    }
  };

  return (
    <Modal show={show} onHide={onHide} size="lg">
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
            <Button
              style={{ backgroundColor: "#007BFF", borderColor: "#007BFF" }}
              onClick={handleSearchMember}
            >
              Tìm kiếm
            </Button>
          </InputGroup>
        )}

        {hasManageRights && searchResults.length > 0 && (
          <div
            className="mb-4 p-3 rounded"
            style={{ backgroundColor: "#EEF2F5" }}
          >
            <h6 className="fw-bold text-secondary">Kết quả tìm kiếm:</h6>
            {searchResults.map((user) => (
              <div
                key={user.userId}
                className="d-flex justify-content-between align-items-center mb-2"
              >
                <span>
                  <span className="fw-semibold">{user.email}</span>
                  {user.alreadyMember && (
                    <span className="text-muted small ms-2">(Đã tham gia)</span>
                  )}
                </span>
                {!user.alreadyMember && (
                  <Button
                    size="sm"
                    style={{
                      backgroundColor: "#28A745",
                      borderColor: "#28A745",
                    }}
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
              <span className="fw-semibold">
                {m.email || "Chưa lấy được email"}
              </span>
            </div>
            <div className="d-flex align-items-center">
              {hasManageRights ? (
                <>
                  <Form.Select
                    size="sm"
                    value={m.role}
                    onChange={(e) => handleChangeRole(m.userId, e.target.value)}
                    style={{ width: "130px", cursor: "pointer" }}
                    className="me-2"
                  >
                    <option value="MANAGER">Quản lý</option>
                    <option value="MEMBER">Thành viên</option>
                  </Form.Select>
                  <Button
                    size="sm"
                    style={{
                      backgroundColor: "#FF5733",
                      borderColor: "#FF5733",
                    }}
                    onClick={() => handleRemoveMember(m.userId)}
                  >
                    Xóa
                  </Button>
                </>
              ) : (
                <span
                  className="badge"
                  style={{
                    backgroundColor:
                      m.role === "MANAGER" ? "#FF8C00" : "#6C757D",
                    color: "#FFF",
                  }}
                >
                  {m.role === "MANAGER" ? "Quản lý" : "Thành viên"}
                </span>
              )}
            </div>
          </div>
        ))}
      </Modal.Body>
    </Modal>
  );
};

export default Member;
