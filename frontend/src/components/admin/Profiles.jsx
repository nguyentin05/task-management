import { useEffect, useState } from "react";
import { Table, Button, Modal, Form } from "react-bootstrap";
import { authApis } from "../../configs/Apis";

const Profiles = () => {
  const [profiles, setProfiles] = useState([]);

  const [selectedProfile, setSelectedProfile] = useState(null);

  const [showEdit, setShowEdit] = useState(false);
  const [showAvatar, setShowAvatar] = useState(false);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [dob, setDob] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");

  const [avatarFile, setAvatarFile] = useState(null);
  const [previewAvatar, setPreviewAvatar] = useState("");

  const loadProfiles = async () => {
    let res = await authApis().get("/profiles", {
      params: {
        page: 0,
        size: 15,
      },
    });

    setProfiles(res.data.result);
  };

  useEffect(() => {
    loadProfiles();
  }, []);

  const viewProfile = async (id) => {
    let res = await authApis().get(`/profiles/${id}`);

    let p = res.data.result;

    setSelectedProfile(p);

    setFirstName(p.firstName || "");
    setLastName(p.lastName || "");
    setDob(p.dob || "");
    setPhoneNumber(p.phoneNumber || "");

    setPreviewAvatar(p.avatar || "");

    setShowEdit(true);
  };

  const updateProfile = async () => {
    await authApis().patch(`/profiles/${selectedProfile.id}`, {
      firstName,
      lastName,
      dob,
      phoneNumber,
    });

    setShowEdit(false);
    loadProfiles();
  };

  const openAvatarModal = (profile) => {
    setSelectedProfile(profile);
    setPreviewAvatar(profile.avatar);
    setShowAvatar(true);
  };

  const handleAvatarChange = (e) => {
    let file = e.target.files[0];

    if (file) {
      setAvatarFile(file);
      setPreviewAvatar(URL.createObjectURL(file));
    }
  };

  const updateAvatar = async () => {
    let form = new FormData();
    form.append("avatar", avatarFile);

    await authApis().put(`/profiles/${selectedProfile.id}/avatar`, form, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    setShowAvatar(false);
    setAvatarFile(null);

    loadProfiles();
  };

  return (
    <div>
      <h2>Thông tin người dùng</h2>

      <Table bordered>
        <thead>
          <tr>
            <th>UID</th>
            <th>Họ và tên</th>
            <th>Ngày sinh</th>
            <th>Số điện thoại</th>
            <th>Avatar</th>
            <th>Thao tác</th>
          </tr>
        </thead>

        <tbody>
          {profiles.map((p) => (
            <tr key={p.id}>
              <td>{p.userId}</td>

              <td>
                {p.lastName} {p.firstName}
              </td>

              <td>{p.dob}</td>

              <td>{p.phoneNumber}</td>

              <td>
                {p.avatar && <img src={p.avatar} alt="avatar" width="50" />}
              </td>

              <td>
                <Button className="me-2" onClick={() => viewProfile(p.id)}>
                  Sửa
                </Button>

                <Button variant="secondary" onClick={() => openAvatarModal(p)}>
                  Avatar
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={showEdit} onHide={() => setShowEdit(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh sửa thông tin</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          <Form.Control
            className="mb-2"
            placeholder="Nhập họ"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
          />

          <Form.Control
            className="mb-2"
            placeholder="Nhập tên"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
          />

          <Form.Control
            className="mb-2"
            type="date"
            value={dob || ""}
            onChange={(e) => setDob(e.target.value)}
          />

          <Form.Control
            className="mb-2"
            placeholder="Nhập số điện thoại"
            value={phoneNumber || ""}
            onChange={(e) => setPhoneNumber(e.target.value)}
          />
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowEdit(false)}>
            Hủy
          </Button>

          <Button onClick={updateProfile}>Lưu</Button>
        </Modal.Footer>
      </Modal>

      <Modal show={showAvatar} onHide={() => setShowAvatar(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Đổi Avatar</Modal.Title>
        </Modal.Header>

        <Modal.Body>
          {previewAvatar && (
            <img
              src={previewAvatar}
              alt="preview"
              width="120"
              className="mb-3"
            />
          )}

          <Form.Control type="file" onChange={handleAvatarChange} />
        </Modal.Body>

        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowAvatar(false)}>
            Cancel
          </Button>

          <Button onClick={updateAvatar}>Tải lên</Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default Profiles;
