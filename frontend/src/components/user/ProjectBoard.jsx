import { useEffect, useState } from "react";
import { Button, Card, Row, Col, Modal, Form } from "react-bootstrap";
import { authApis } from "../../configs/Apis";

const ProjectBoard = ({ projectId }) => {
  const [columns, setColumns] = useState([]);
  const [show, setShow] = useState(false);

  const [column, setColumn] = useState({
    name: "",
    position: 0,
  });

  const loadBoard = async () => {
    let res = await authApis().get(`/projects/${projectId}/columns`);

    setColumns(res.data.result);
  };

  useEffect(() => {
    loadBoard();
  }, []);

  const createColumn = async (e) => {
    e.preventDefault();

    await authApis().post(`/projects/${projectId}/columns`, column);

    setShow(false);
    loadBoard();
  };

  return (
    <div>
      <h3>Không gian làm việc</h3>

      <Button onClick={() => setShow(true)}>Tạo cột</Button>

      <Row className="mt-3">
        {columns.map((c) => (
          <Col key={c.id} md={3}>
            <Card>
              <Card.Header>{c.name}</Card.Header>

              <Card.Body>
                {c.tasks?.map((t) => (
                  <Card key={t.id} className="mb-2">
                    <Card.Body>{t.name}</Card.Body>
                  </Card>
                ))}
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>

      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo cột</Modal.Title>
        </Modal.Header>

        <Form onSubmit={createColumn}>
          <Modal.Body>
            <Form.Control
              placeholder="Column name"
              onChange={(e) => setColumn({ ...column, name: e.target.value })}
            />
          </Modal.Body>

          <Modal.Footer>
            <Button variant="danger" onClick={() => setShow(false)}>
              Hủy
            </Button>

            <Button type="submit">Tạo</Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};

export default ProjectBoard;
