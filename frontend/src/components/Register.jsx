import { useState, useContext } from "react";
import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import Apis, { endpoints } from "../configs/Apis";
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "../configs/MyContexts";

const Register = () => {
  const info = [
    {
      title: "Tên",
      field: "firstName",
      type: "text",
    },
    {
      title: "Họ và tên lót",
      field: "lastName",
      type: "text",
    },
    {
      title: "Email",
      field: "email",
      type: "email",
    },
    {
      title: "Mật khẩu",
      field: "password",
      type: "password",
    },
    {
      title: "Xác nhận mật khẩu",
      field: "confirm",
      type: "password",
    },
  ];

  const [user, setUser] = useState({});
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState();
  const nav = useNavigate();
  const [, dispatch] = useContext(MyUserContext);

  const validate = () => {
    if (user.password != user.confirm) {
      setErr("Mật khẩu không khớp!");
      return false;
    }

    return true;
  };

  const register = async (e) => {
    e.preventDefault();
    if (validate()) {
      try {
        setLoading(true);

        const requestData = { ...user };
        delete requestData.confirm;

        let res = await Apis.post(endpoints["register"], requestData);

        if (res.data.code === 1000) {
          alert("Đăng ký thành công!");
          nav("/login");
        }
      } catch (ex) {
        switch (ex.response.data.code) {
          case 2003:
            setErr("Mật khẩu quá yếu!");
            break;
          case 6001:
            setErr("Người dùng đã tồn tại!");
            break;
          default:
            setErr("Lỗi!");
        }
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <>
      <h1 className="text-center text-secondary mt-1">ĐĂNG KÝ TÀI KHOẢN</h1>

      {err && (
        <Alert variant="danger" className="mt-2">
          {err}
        </Alert>
      )}

      <Form onSubmit={register}>
        {info.map((i) => (
          <Form.Group key={i.field} className="mb-3" controlId={i.field}>
            <Form.Label>{i.title}</Form.Label>
            <Form.Control
              value={user[i.field]}
              onChange={(e) => setUser({ ...user, [i.field]: e.target.value })}
              type={i.type}
              placeholder={i.title}
              required
            />
          </Form.Group>
        ))}
        {loading ? (
          <MySpinner />
        ) : (
          <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
            <Button variant="success" type="submit">
              Đăng ký
            </Button>
          </Form.Group>
        )}
      </Form>
    </>
  );
};

export default Register;
