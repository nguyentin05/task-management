import { Link } from "react-router-dom";
import { Container, Row, Col, Card } from "react-bootstrap";

const Admin = () => {
  return (
    <Container className="mt-5 pb-5" style={{ minHeight: "75vh" }}>
      <div className="text-center mb-5 mt-5">
        <h2 className="fw-bold" style={{ color: "#6C757D" }}>
          Trang quản trị
        </h2>
      </div>

      <Row className="justify-content-center">
        <Col md={5} lg={4} className="mb-4">
          <Card
            as={Link}
            to="/admin/users"
            className="h-100 shadow-sm border-0 text-decoration-none text-center rounded-4 project-card"
            style={{
              transition: "all 0.3s ease",
              cursor: "pointer",
              display: "block",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-10px)";
              e.currentTarget.classList.add("shadow-lg");
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.classList.remove("shadow-lg");
            }}
          >
            <Card.Body className="p-5 d-flex flex-column justify-content-center align-items-center">
              <Card.Title className="fw-bold fs-3 text-dark mb-4">
                Quản lý người dùng
              </Card.Title>
              <Card.Text className="text-muted small">
                Tất cả thông tin người dùng của hệ thống.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>

        <Col md={5} lg={4} className="mb-4">
          <Card
            as={Link}
            to="/admin/workspaces"
            className="h-100 shadow-sm border-0 text-decoration-none text-center rounded-4 project-card"
            style={{
              transition: "all 0.3s ease",
              cursor: "pointer",
              display: "block",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-10px)";
              e.currentTarget.classList.add("shadow-lg");
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.classList.remove("shadow-lg");
            }}
          >
            <Card.Body className="p-5 d-flex flex-column justify-content-center align-items-center">
              <Card.Title className="fw-bold fs-3 text-dark mb-4">
                Quản lý không gian làm việc
              </Card.Title>
              <Card.Text className="text-muted small">
                Tất cả không gian làm việc và các dự án.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Admin;
