import { useState, useEffect } from "react";
import { Button, Form, Alert } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layout/MySpinner";

const Profile = () => {
  const info = [
    { title: "Họ",            field: "lastName",    type: "text" },
    { title: "Tên",           field: "firstName",   type: "text" },
    { title: "Ngày sinh",     field: "dob",         type: "date" },
    { title: "Số điện thoại", field: "phoneNumber", type: "text" },
  ];

  const [user, setUser] = useState({});
  const [avatar, setAvatar] = useState(null);
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);
  const [err, setErr] = useState(null);
  const [success, setSuccess] = useState(null);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const res = await authApis().get(endpoints["profiles-me"]);
      setUser(res.data.result);
    } catch (ex) {
      setErr(ex.response?.data?.message || "Không thể tải thông tin!");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  const updateProfile = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setErr(null);
      setSuccess(null);

      const res = await authApis().patch(endpoints["update-profiles"], user);

      if (avatar) {
        const form = new FormData();
        form.append("file", avatar);
        await authApis().put(endpoints["update-avatar-me"], form, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }

      setUser(res.data.result);
      setEditing(false);
      setAvatar(null);
      setSuccess("Cập nhật thành công!");
    } catch (ex) {
      setErr(ex.response?.data?.message || "Cập nhật thất bại, vui lòng thử lại!");
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <div>
      <h3 className="text-secondary">
        Hôm nay: {new Date().toLocaleDateString("vi-VN")}
      </h3>
      <h2>Thông tin của bạn:</h2>

      {err && <Alert variant="danger" className="mt-2">{err}</Alert>}
      {success && <Alert variant="success" className="mt-2">{success}</Alert>}

      {!editing ? (
        <>
          <div className="card p-3">
            <img
              src={user.avatarUrl}
              alt="Avatar"
              width="120"
              className="mb-3 rounded-circle"
            />
            <h3>Họ và tên: {user.firstName} {user.lastName}</h3>
            <h3>Ngày sinh: {user.dob}</h3>
            <h3>Số điện thoại: {user.phoneNumber}</h3>
          </div>
          <Button
            variant="success"
            className="mt-3"
            onClick={() => {
              setSuccess(null);
              setErr(null);
              setEditing(true);
            }}
          >
            Chỉnh sửa thông tin
          </Button>
        </>
      ) : (
        <Form onSubmit={updateProfile}>
          <div className="card p-3">
            <img
              src={avatar ? URL.createObjectURL(avatar) : user.avatarUrl}
              alt="Avatar"
              width="120"
              className="mb-3 rounded-circle"
            />
            <Form.Group className="mb-3">
              <Form.Label>Avatar</Form.Label>
              <Form.Control
                type="file"
                accept="image/*"
                onChange={(e) => setAvatar(e.target.files[0])}
              />
            </Form.Group>

            {info.map((i) => (
              <Form.Group key={i.field} className="mb-3" controlId={i.field}>
                <Form.Label>{i.title}</Form.Label>
                <Form.Control
                  value={user[i.field] || ""}
                  onChange={(e) => setUser({ ...user, [i.field]: e.target.value })}
                  type={i.type}
                />
              </Form.Group>
            ))}
          </div>

          <Button variant="success" type="submit" className="mt-2">
            Lưu
          </Button>
          <Button
            variant="danger"
            className="mt-2 ms-2"
            onClick={() => {
              setEditing(false);
              setAvatar(null);
              setErr(null);
            }}
          >
            Hủy
          </Button>
        </Form>
      )}
    </div>
  );
};

export default Profile;