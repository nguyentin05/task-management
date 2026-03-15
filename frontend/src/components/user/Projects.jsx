import { useState } from "react";
import { Button, Modal, Form, Alert } from "react-bootstrap";
import { authApis } from "../../configs/Apis";

const Projects = () => {
  const [show, setShow] = useState(false);

  const [project, setProject] = useState({
    name: "",
    description: "",
    startAt: "",
    endAt: "",
  });

  const [msg, setMsg] = useState("");

  const createProject = async (e) => {
    e.preventDefault();

    await authApis().post("/projects", project);

    setMsg("Tạo project thành công");
    setShow(false);
  };

  return (
    <div>
      <h2>Projects</h2>

      {msg && <Alert variant="success">{msg}</Alert>}

      <Button onClick={() => setShow(true)}>Tạo Project</Button>

      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Tạo Project</Modal.Title>
        </Modal.Header>

        <Form onSubmit={createProject}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Tên project</Form.Label>
              <Form.Control
                onChange={(e) =>
                  setProject({ ...project, name: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                onChange={(e) =>
                  setProject({ ...project, description: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Bắt đầu</Form.Label>
              <Form.Control
                type="datetime-local"
                onChange={(e) =>
                  setProject({ ...project, startAt: e.target.value })
                }
              />
            </Form.Group>

            <Form.Group>
              <Form.Label>Kết thúc</Form.Label>
              <Form.Control
                type="datetime-local"
                onChange={(e) =>
                  setProject({ ...project, endAt: e.target.value })
                }
              />
            </Form.Group>
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

export default Projects;
