import { Link } from "react-router-dom";
import { Button } from "react-bootstrap";

const Admin = () => {
  return (
    <div>
      <h2>Trang Quản trị Admin</h2>

      <div className="mt-3">
        <Link to="/admin/users">
          <Button className="me-2">Người dùng</Button>
        </Link>

        <Link to="/admin/roles">
          <Button className="me-2">Role</Button>
        </Link>

        <Link to="/admin/permissions">
          <Button className="me-2">Quyền</Button>
        </Link>

        <Link to="/admin/profiles">
          <Button>Thông tin người dùng</Button>
        </Link>
      </div>
    </div>
  );
};

export default Admin;
