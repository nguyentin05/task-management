import { Row, Col, Card } from "react-bootstrap";

const Statistic = ({ stats, members }) => {
  const totalTasks = stats?.totalTasks || 0;
  const completedTasks = stats?.completedTasks || 0;

  const completionRate = stats?.completionRate
    ? Math.round(stats.completionRate)
    : 0;

  const totalMembers = stats?.totalMembers || members?.length || 0;

  return (
    <>
      <h5 className="fw-bold text-secondary mb-3 mt-4">
        Thống kê dự án {stats?.projectName ? `- ${stats.projectName}` : ""}
      </h5>
      <Row>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="fw-bold" style={{ color: "#007BFF" }}>
              {totalTasks}
            </h3>
            <span className="text-muted small">Tổng công việc</span>
          </Card>
        </Col>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="fw-bold" style={{ color: "#28A745" }}>
              {completedTasks}
            </h3>
            <span className="text-muted small">Đã hoàn thành</span>
          </Card>
        </Col>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="fw-bold" style={{ color: "#FF8C00" }}>
              {completionRate}%
            </h3>
            <span className="text-muted small">Tỷ lệ hoàn thành</span>
          </Card>
        </Col>
        <Col md={3} sm={6} className="mb-3">
          <Card className="border-0 shadow-sm text-center py-3 rounded-4">
            <h3 className="fw-bold" style={{ color: "#6F42C1" }}>
              {totalMembers}
            </h3>
            <span className="text-muted small">Thành viên tham gia</span>
          </Card>
        </Col>
      </Row>
    </>
  );
};

export default Statistic;
