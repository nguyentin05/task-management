import { Button, Form } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import { useContext, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import Apis, { authApis, endpoints } from "../configs/Apis";
import cookie from "react-cookies";
import { MyUserContext } from "../configs/MyContexts";
import Swal from "sweetalert2";

const Login = () => {
  const [user, setUser] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();
  const [, dispatch] = useContext(MyUserContext);
  const loadingStartTime = useRef(null);

  const info = [
    { title: "Email", field: "email", type: "text" },
    { title: "Mật khẩu", field: "password", type: "password" },
  ];

  const ensureSpinnerMinTime = () => {
    if (!loadingStartTime.current) return Promise.resolve();
    const displayTime = Date.now() - loadingStartTime.current;
    const minDisplay = 500;
    if (displayTime < minDisplay) {
      return new Promise((resolve) =>
        setTimeout(resolve, minDisplay - displayTime),
      );
    }
    return Promise.resolve();
  };

  const login = async (e) => {
    e.preventDefault();

    const delayTimer = setTimeout(() => {
      setLoading(true);
      loadingStartTime.current = Date.now();
    }, 300);

    try {
      const res = await Apis.post(endpoints["login"], user);

      await ensureSpinnerMinTime();

      const token = res.data.result.token;
      cookie.save("token", token, { path: "/" });

      const api = authApis();
      const [userRes, profileRes] = await Promise.all([
        api.get(endpoints["me"]),
        api.get(endpoints["profiles-me"]).catch(() => null),
      ]);

      const userData = userRes.data.result;
      const profileData =
        profileRes?.data?.code === 1000 ? profileRes.data.result : null;

      const currentUser = { ...userData, profile: profileData };

      dispatch({ type: "login", payload: currentUser });

      await Swal.fire({
        title: "Đăng nhập thành công!",
        icon: "success",
        timer: 1000,
        showConfirmButton: false,
      });

      nav("/");
    } catch (ex) {
      await ensureSpinnerMinTime();

      let errorMessage =
        ex.response?.data?.message || "Có lỗi xảy ra, vui lòng thử lại sau!";
      const errorCode = ex.response?.data?.code;

      if (errorCode === 3001 || errorCode === 3004) {
        info.forEach((item) => {
          errorMessage = errorMessage.replace(item.field, item.title);
        });
      }

      Swal.fire({
        title: "Đăng nhập thất bại!",
        text: errorMessage,
        icon: "error",
        confirmButtonText: "Thử lại",
      });
    } finally {
      clearTimeout(delayTimer);
      setLoading(false);
      loadingStartTime.current = null;
    }
  };

  return (
    <>
      <h1 className="text-center mt-5 mb-4" style={{ color: "#6C757D" }}>
        ĐĂNG NHẬP
      </h1>

      <Form
        onSubmit={login}
        className="border p-4 rounded shadow-sm mx-auto bg-white"
        style={{ maxWidth: "500px" }}
      >
        {info.map((i) => (
          <Form.Group key={i.field} className="mb-3" controlId={i.field}>
            <Form.Label className="fw-bold">{i.title}</Form.Label>
            <Form.Control
              value={user[i.field] || ""}
              onChange={(e) => setUser({ ...user, [i.field]: e.target.value })}
              type={i.type}
              placeholder={`Nhập ${i.title.toLowerCase()}`}
            />
          </Form.Group>
        ))}

        <div className="d-flex justify-content-center mt-4">
          {loading ? (
            <MySpinner />
          ) : (
            <Button
              type="submit"
              size="lg"
              style={{
                backgroundColor: "#28A745",
                borderColor: "#28A745",
                width: "200px",
                fontWeight: "600",
              }}
            >
              Đăng nhập
            </Button>
          )}
        </div>
      </Form>
    </>
  );
};

export default Login;
