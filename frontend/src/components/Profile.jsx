import { useState, useEffect, useRef } from "react";
import {
  Container, Button, Form, Modal, Badge, Row, Col, Card,
} from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layout/MySpinner";
import Swal from "sweetalert2";

const Profile = () => {
  const [loading, setLoading] = useState(true);
  const loadingStartTime = useRef(null);

  const [userInfo, setUserInfo] = useState(null);
  const [profileInfo, setProfileInfo] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    firstName: "", lastName: "", dob: "", phoneNumber: "",
  });

  const [avatarFile, setAvatarFile] = useState(null);
  const [previewAvatar, setPreviewAvatar] = useState(null);

  const [showPwdModal, setShowPwdModal] = useState(false);
  const [pwdForm, setPwdForm] = useState({
    oldPassword: "", newPassword: "", confirmPassword: "",
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
      const [userRes, profileRes] = await Promise.all([
        api.get(endpoints["me"]),
        api.get(endpoints["profiles-me"]).catch(() => null),
      ]);

      await ensureSpinnerMinTime();

      if (userRes.data.code === 1000) setUserInfo(userRes.data.result);

      if (profileRes?.data?.code === 1000) {
        const pData = profileRes.data.result;
        setProfileInfo(pData);
        setEditForm({
          firstName: pData.firstName || "",
          lastName: pData.lastName || "",
          dob: pData.dob || "",
          phoneNumber: pData.phoneNumber || "",
        });
        setPreviewAvatar(pData.avatar || null);
      }
    } catch (ex) {
      await ensureSpinnerMinTime();
      console.error("Lỗi tải thông tin cá nhân:", ex);
      Swal.fire("Lỗi", "Không thể tải dữ liệu hồ sơ", "error");
    } finally {
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    try {
      const res = await authApis().patch(endpoints["update-profiles"], editForm);
      if (res.data.code === 1000) {
        setProfileInfo(res.data.result);
        setIsEditing(false);
        Swal.fire({ icon: "success", title: "Thành công", text: "Đã cập nhật hồ sơ", timer: 1500 });
      }
    } catch (ex) {
      Swal.fire("Lỗi", ex.response?.data?.message || "Không thể cập nhật hồ sơ!", "error");
    }
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const allowedTypes = ["image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"];
    if (!allowedTypes.includes(file.type)) {
      Swal.fire("Lỗi", "Định dạng ảnh không hợp lệ. Vui lòng chọn JPEG, PNG, GIF hoặc WEBP.", "warning");
      setAvatarFile(null);
      setPreviewAvatar(null);
      return;
    }

    setAvatarFile(file);
    setPreviewAvatar(URL.createObjectURL(file));
  };

  const handleUploadAvatar = async () => {
    if (!avatarFile) return;
    try {
      const formData = new FormData();
      formData.append("avatar", avatarFile);

      const res = await authApis().put(endpoints["update-avatar-me"], formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      if (res.data.code === 1000) {
        setProfileInfo({ ...profileInfo, avatar: res.data.result.avatar });
        setAvatarFile(null);
        Swal.fire({ icon: "success", title: "Thành công", text: "Đã thay đổi ảnh đại diện", timer: 1500 });
      }
    } catch (ex) {
      Swal.fire("Lỗi", ex.response?.data?.message || "Tải ảnh lên thất bại!", "error");
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (pwdForm.newPassword !== pwdForm.confirmPassword) {
      return Swal.fire("Lỗi", "Mật khẩu xác nhận không khớp!", "warning");
    }
    try {
      const res = await authApis().put(endpoints["change-password"], {
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword,
      });

      if (res.data.code === 1000) {
        Swal.fire({ icon: "success", title: "Thành công", text: res.data.message, timer: 1500 });
        setShowPwdModal(false);
        setPwdForm({ oldPassword: "", newPassword: "", confirmPassword: "" });
      }
    } catch (ex) {
      Swal.fire("Lỗi", ex.response?.data?.message || "Đổi mật khẩu thất bại!", "error");
    }
  };

  if (loading) return <MySpinner />;

  return (
    <Container className="py-5" style={{ minHeight: "80vh" }}>
      <Row className="justify-content-center">
        {/* Cột avatar + thông tin tóm tắt */}
        <Col md={4} className="mb-4">
          <Card className="border-0 shadow-sm rounded-4 text-center p-4">
            <div
              className="position-relative d-inline-block mx-auto mb-3"
              style={{ width: "150px", height: "150px" }}
            >
              {previewAvatar ? (
                <img
                  src={previewAvatar}
                  alt="Avatar"
                  className="rounded-circle object-fit-cover w-100 h-100 border border-3 border-light shadow-sm"
                />
              ) : (
                <div className="bg-secondary text-white rounded-circle d-flex justify-content-center align-items-center w-100 h-100 fs-1 shadow-sm">
                  {profileInfo?.firstName?.charAt(0) || userInfo?.email?.charAt(0).toUpperCase()}
                </div>
              )}
              <label
                className="position-absolute bottom-0 end-0 bg-primary text-white rounded-circle p-2 shadow"
                style={{ cursor: "pointer", transform: "translate(-10px, -10px)" }}
                title="Thay đổi ảnh đại diện"
              >
                <i className="bi bi-camera-fill"></i>
                <input type="file" hidden accept="image/*" onChange={handleAvatarChange} />
              </label>
            </div>

            {avatarFile && (
              <div className="mb-3">
                <Button variant="success" size="sm" className="rounded-pill px-3 shadow-sm" onClick={handleUploadAvatar}>
                  <i className="bi bi-upload me-1"></i> Lưu ảnh mới
                </Button>
                <Button
                  variant="light" size="sm" className="rounded-pill px-3 ms-2"
                  onClick={() => { setAvatarFile(null); setPreviewAvatar(profileInfo?.avatar || null); }}
                >
                  Hủy
                </Button>
              </div>
            )}

            <h4 className="fw-bold text-dark mt-2 mb-1">
              {profileInfo?.lastName} {profileInfo?.firstName}
            </h4>
            <p className="text-muted small mb-3">{userInfo?.email}</p>

            <div>
              {userInfo?.roles?.map((r) => (
                <Badge key={r.name} bg={r.name === "ADMIN" ? "danger" : "info"} className="me-1 px-3 py-2 rounded-pill">
                  {r.name}
                </Badge>
              ))}
            </div>
          </Card>
        </Col>

        {/* Cột form thông tin */}
        <Col md={8}>
          <Card className="border-0 shadow-sm rounded-4 p-4">
            <div className="d-flex justify-content-between align-items-center border-bottom pb-3 mb-4">
              <h5 className="fw-bold mb-0" style={{ color: "#6C757D" }}>Thông tin cá nhân</h5>
              <div>
                <Button
                  variant={isEditing ? "outline-secondary" : "outline-primary"}
                  size="sm" className="rounded-pill px-3 me-2"
                  onClick={() => setIsEditing(!isEditing)}
                >
                  {isEditing ? "Hủy sửa" : "Chỉnh sửa"}
                </Button>
                <Button variant="outline-warning" size="sm" className="rounded-pill px-3" onClick={() => setShowPwdModal(true)}>
                  Đổi mật khẩu
                </Button>
              </div>
            </div>

            <Form onSubmit={handleSaveProfile}>
              <Row>
                {[
                  { label: "Họ (Last Name)",  field: "lastName",    type: "text" },
                  { label: "Tên (First Name)", field: "firstName",   type: "text" },
                  { label: "Ngày sinh",        field: "dob",         type: "date" },
                  { label: "Số điện thoại",    field: "phoneNumber", type: "text" },
                ].map((i) => (
                  <Col md={6} className="mb-4" key={i.field}>
                    <Form.Label className="small text-muted fw-bold">{i.label}</Form.Label>
                    <Form.Control
                      type={isEditing ? i.type : "text"}
                      value={
                        isEditing
                          ? editForm[i.field]
                          : i.field === "dob"
                            ? profileInfo?.dob ? new Date(profileInfo.dob).toLocaleDateString() : "—"
                            : profileInfo?.[i.field] || "—"
                      }
                      onChange={(e) => setEditForm({ ...editForm, [i.field]: e.target.value })}
                      readOnly={!isEditing}
                      className={!isEditing ? "bg-light border-0" : ""}
                    />
                  </Col>
                ))}
              </Row>

              {isEditing && (
                <div className="text-end mt-2">
                  <Button variant="success" type="submit" className="rounded-pill px-4 shadow-sm">
                    Lưu thay đổi
                  </Button>
                </div>
              )}
            </Form>
          </Card>
        </Col>
      </Row>

      {/* Modal đổi mật khẩu */}
      <Modal show={showPwdModal} onHide={() => setShowPwdModal(false)} centered>
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="fw-bold">Đổi mật khẩu</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleChangePassword}>
          <Modal.Body>
            {[
              { label: "Mật khẩu hiện tại", field: "oldPassword", placeholder: "Nhập mật khẩu cũ..." },
              { label: "Mật khẩu mới",      field: "newPassword",  placeholder: "Nhập mật khẩu mới..." },
              { label: "Xác nhận mật khẩu mới", field: "confirmPassword", placeholder: "Nhập lại mật khẩu mới..." },
            ].map((i) => (
              <Form.Group className="mb-3" key={i.field}>
                <Form.Label className="small fw-bold">{i.label}</Form.Label>
                <Form.Control
                  type="password"
                  placeholder={i.placeholder}
                  value={pwdForm[i.field]}
                  onChange={(e) => setPwdForm({ ...pwdForm, [i.field]: e.target.value })}
                  isInvalid={
                    i.field === "confirmPassword" &&
                    pwdForm.confirmPassword &&
                    pwdForm.newPassword !== pwdForm.confirmPassword
                  }
                />
                {i.field === "confirmPassword" && (
                  <Form.Control.Feedback type="invalid">
                    Mật khẩu xác nhận không khớp!
                  </Form.Control.Feedback>
                )}
              </Form.Group>
            ))}
          </Modal.Body>
          <Modal.Footer className="border-0">
            <Button variant="light" onClick={() => setShowPwdModal(false)}>Hủy</Button>
            <Button variant="warning" type="submit" className="fw-bold">Xác nhận đổi</Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Profile;