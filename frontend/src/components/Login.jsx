import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import Apis, { authApis, endpoints } from "../configs/Apis";
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

  const [user, setUser] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState(null);
  const nav = useNavigate();
  const [, dispatch] = useContext(MyUserContext);

  const login = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setErr(null);

      let res = await Apis.post(endpoints["login"], user);

      if (res.data.code === 1000) {
        const token = res.data.result.token;
        cookie.save("token", token);

        const meRes = await Apis.get(endpoints["me"], {
          headers: { Authorization: `Bearer ${token}` }
        });

        dispatch({ 
          type: "login", 
          payload: meRes.data.result
        });

        nav("/");
      }
    } catch (ex) {
      console.error("Lỗi:", ex.response?.status, ex.response?.data);
      console.error("Chi tiết lỗi:", ex);
      setErr(ex.response?.data?.message || "Đăng nhập thất bại, vui lòng thử lại!");
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
              value={user[i.field] || ""}
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
          <Form.Group className="mb-3">
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
