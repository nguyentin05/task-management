import { useState, useContext, useRef } from "react";
import { Button, Form } from "react-bootstrap";
import MySpinner from "./layout/MySpinner";
import Apis, { endpoints } from "../configs/Apis";
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "../configs/MyContexts";
import Swal from "sweetalert2";

const Register = () => {
  const info = [
    {
      title: "Họ và tên lót",
      field: "lastName",
      type: "text",
    },
    {
      title: "Tên",
      field: "firstName",
      type: "text",
    },
    {
      title: "Email",
      field: "email",
      type: "text",
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
  const nav = useNavigate();
  const loadingStartTime = useRef(null);

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

  const register = async (e) => {
    e.preventDefault();

    if (user.password !== user.confirm) {
      Swal.fire({
        title: "Lỗi!",
        text: "Mật khẩu không khớp!",
        icon: "error",
      });
      return;
    }

    const delayTimer = setTimeout(() => {
      setLoading(true);
      loadingStartTime.current = Date.now();
    }, 300);

    try {
      const requestData = { ...user };
      delete requestData.confirm;

      let res = await Apis.post(endpoints["register"], requestData);

      await ensureSpinnerMinTime();

      if (res.data.code === 1000) {
        await Swal.fire({
          title: "Đăng ký thành công!",
          text: "Bạn sẽ được chuyển sang trang đăng nhập.",
          icon: "success",
          timer: 3000,
          showConfirmButton: false,
        });

        nav("/login");
      }
    } catch (ex) {
      await ensureSpinnerMinTime();

      const serverData = ex.response?.data;
      const errorMsg =
        serverData?.message || "Có lỗi xảy ra, vui lòng thử lại sau!";

      Swal.fire({
        title: "Đăng ký thất bại!",
        text: errorMsg,
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
        ĐĂNG KÝ TÀI KHOẢN
      </h1>

      <Form
        onSubmit={register}
        className="border p-4 rounded shadow-sm mx-auto"
        style={{ maxWidth: "500px" }}
      >
        {info.map((i) => (
          <Form.Group key={i.field} className="mb-3" controlId={i.field}>
            <Form.Label className="fw-bold">{i.title}</Form.Label>
            <Form.Control
              value={user[i.field]}
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
              Đăng ký
            </Button>
          )}
        </div>
      </Form>
    </>
  );
};

export default Register;
