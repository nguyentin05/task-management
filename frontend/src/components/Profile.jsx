import { useState } from "react";
import { Button, Form } from "react-bootstrap";

const Profile = () => {
  const info = [
    {
      title: "Avatar",
      field: "avatarUrl",
      type: "url",
    },
    {
      title: "Họ và tên",
      field: "fullName",
      type: "text",
    },
    {
      title: "Email",
      field: "email",
      type: "email",
    },
    {
      title: "Số điện thoại",
      field: "phoneNumber",
      type: "text",
    },
  ];

  const date = new Date().toLocaleDateString("vi-VN");
  const [user, setUser] = useState({});
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);

  return (
    <>
      <div>
        <h3 className="text-secondary">Hôm nay: {date}</h3>
        <h2>Thông tin của bạn:</h2>

        {!editing ? (
          <>
            <div className="card">
              <img src={user.avatar} alt="Avatar" />
              <h3>
                Họ và tên: {user.firstName} {user.lastName}
              </h3>
              <h3>Email: {user.email}</h3>
              <h3>Số điện thoại: {user.phoneNumber}</h3>
            </div>
            <Button variant="success" onClick={() => setEditing(true)}>
              Chỉnh sửa thông tin
            </Button>
          </>
        ) : (
          <>
            <Form>
              <div className="card">
                {info.map((i) => (
                  <Form.Group
                    key={i.field}
                    className="mb-3"
                    controlId={i.field}
                  >
                    <Form.Label>{i.title}</Form.Label>
                    <Form.Control
                      value={user[i.field]}
                      onChange={(e) =>
                        setUser({ ...user, [i.field]: e.target.value })
                      }
                      type={i.type}
                      default={user.field}
                    ></Form.Control>
                  </Form.Group>
                ))}
              </div>
              <Button variant="success" type="submit">
                Lưu
              </Button>
            </Form>
          </>
        )}
      </div>
    </>
  );
};

export default Profile;
