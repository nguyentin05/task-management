const Footer = () => {
  const students = ["Nguyễn Trọng Tín", "Trần Anh Tú", "Trần Thanh Tung"];

  return (
    <footer className="mt-5 pb-4 border-top">
      <div className="container text-center pt-4" style={{ color: "#6C757D" }}>
        <p className="fw-bold mb-2">Hệ thống được phát triển bởi:</p>

        {students.map((name, index) => (
          <p key={index} className="mb-1">
            {name}
          </p>
        ))}

        <p className="mt-3">
          <strong>Trường Đại học Mở Thành phố Hồ Chí Minh</strong>
        </p>
        <p className="mt-3">
          <strong>Khoa Công nghệ thông tin</strong>
        </p>

        <div className="mt-4 opacity-75" style={{ fontSize: "0.9rem" }}>
          © 2026 - Task Management Project.
        </div>
      </div>
    </footer>
  );
};

export default Footer;
