import { useState, useEffect } from "react";
import { Button, Form, Alert } from "react-bootstrap";
import { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layout/MySpinner";

const Profile = () => {
  const info = [
    {
      title: "Họ",
      field: "lastName",
      type: "text",
    },
    {
      title: "Tên",
      field: "firstName",
      type: "text",
    },
    {
      title: "Ngày sinh",
      field: "dob",
      type: "date",
    },
    {
      title: "Số điện thoại",
      field: "phoneNumber",
      type: "text",
    },
  ];

  const date = new Date().toLocaleDateString("vi-VN");
  const [user, setUser] = useState({});
  const [avatar, setAvatar] = useState();
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);

  const loadProfile = async () => {
    try {
      setLoading(true);
      let res = await authApis().get(endpoints["profile-me"]);
      setUser(res.data.result);
    } catch (ex) {
      console.error(ex);
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

      await authApis().patch(endpoints["update-profile"], user);

      if (avatar) {
        const form = new FormData();
        form.append("file", avatar);

        await authApis().put(endpoints["update-avatar"], form, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });
      }

      alert("Cập nhật thành công!");
      setEditing(false);
      loadProfile();
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <MySpinner />;

  return (
    <>
      <div>
        <h3 className="text-secondary">Hôm nay: {date}</h3>
        <h2>Thông tin của bạn:</h2>

        {!editing ? (
          <>
            <div className="card p-3">
              <img
                src={user.avatarUrl}
                alt="Avatar"
                width="120"
                className="mb-3 rounded-circle"
              />

              <h3>
                Họ và tên: {user.firstName} {user.lastName}
              </h3>
              <h3>Ngày sinh: {user.dob}</h3>
              <h3>Số điện thoại: {user.phoneNumber}</h3>
            </div>
            <Button
              variant="success"
              className="mt-3"
              onClick={() => setEditing(true)}
            >
              Chỉnh sửa thông tin
            </Button>
          </>
        ) : (
          <>
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
                    onChange={(e) => setAvatar(e.target.files[0])}
                  />
                </Form.Group>

                {info.map((i) => (
                  <Form.Group
                    key={i.field}
                    className="mb-3"
                    controlId={i.field}
                  >
                    <Form.Label>{i.title}</Form.Label>
                    <Form.Control
                      value={user[i.field] || ""}
                      onChange={(e) =>
                        setUser({ ...user, [i.field]: e.target.value })
                      }
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
                }}
              >
                Hủy
              </Button>
            </Form>
          </>
        )}
      </div>
    </>
  );
};

export default Profile;
