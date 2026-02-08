import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../configs/Apis";
import cookie from "react-cookies";
import { MyUserContext } from "../configs/MyContexts";

const Login = () => {
  const info = [
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
  ];

  const [user, setUser] = useState({});
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState();
  const nav = useNavigate();
  const [, dispatch] = useContext(MyUserContext);

  const login = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      let res = await Apis.post(endpoints["login"], { ...user });

      if (res.data.code === 1000) {
        console.info(res.data);
        cookie.save("token", res.data.result.accessToken);

        dispatch({
          type: "login",
        });

        alert("Đăng nhập thành công!");

        nav("/");
      }
    } catch (ex) {
      if (ex.response.status === 500)
        setErr("Thông tin tài khoản hoặc mật khẩu sai, vui lòng kiểm tra lại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <h1 className="text-center text-secondary mt-1">ĐĂNG NHẬP</h1>

      {err && (
        <Alert variant="danger" className="mt-2">
          {err}
        </Alert>
      )}

      <Form onSubmit={login}>
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
              Đăng nhập
            </Button>
          </Form.Group>
        )}
      </Form>
    </>
  );
};

export default Login;
