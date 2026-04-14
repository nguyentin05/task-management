import { useState, useEffect, useMemo } from "react";
import { Form, Button, InputGroup } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import Swal from "sweetalert2";

const Comment = ({
  taskId,
  assigneeId,
  currentUserId,
  hasManageRights,
  members,
}) => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [replyTo, setReplyTo] = useState(null);

  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editContent, setEditContent] = useState("");

  const fetchComments = async () => {
    try {
      const res = await authApis().get(endpoints["get-all-comments"](taskId));
      if (res.data.code === 1000) {
        const data = res.data.result.data || res.data.result;
        setComments(Array.isArray(data) ? data : []);
      }
    } catch (ex) {
      console.error("Lỗi lấy bình luận", ex);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [taskId]);

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    try {
      const payload = { content: newComment };
      if (replyTo) {
        payload.parentCommentId = replyTo.id;
      }

      const res = await authApis().post(
        endpoints["create-comment"](taskId),
        payload,
      );
      if (res.data.code === 1000) {
        setNewComment("");
        setReplyTo(null);
        fetchComments();
      }
    } catch (ex) {
      Swal.fire("Lỗi", "Lỗi gửi bình luận", "error");
    }
  };

  const handleEditComment = async (commentId) => {
    if (!editContent.trim()) return;
    try {
      const res = await authApis().put(endpoints["edit-comment"](commentId), {
        content: editContent,
      });
      if (res.data.code === 1000) {
        setEditingCommentId(null);
        fetchComments();
      }
    } catch (ex) {
      Swal.fire("Lỗi", "Không thể sửa bình luận", "error");
    }
  };

  const handleDeleteComment = async (commentId) => {
    const result = await Swal.fire({
      title: "Xóa bình luận?",
      text: "Bạn có chắc chắn muốn xóa bình luận này không?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#FF5733",
      cancelButtonColor: "#6C757D",
      confirmButtonText: "Xóa",
    });

    if (result.isConfirmed) {
      try {
        const res = await authApis().delete(
          endpoints["delete-comment"](commentId),
        );
        if (res.data.code === 1000) {
          fetchComments();
        }
      } catch (ex) {
        Swal.fire("Lỗi", "Không thể xóa bình luận", "error");
      }
    }
  };

  const commentTree = useMemo(() => {
    const map = {};
    const roots = [];

    const clonedComments = JSON.parse(JSON.stringify(comments));

    clonedComments.forEach((c) => {
      map[c.id] = { ...c, children: [] };
    });

    clonedComments.forEach((c) => {
      if (c.parentCommentId && map[c.parentCommentId]) {
        map[c.parentCommentId].children.push(map[c.id]);
      } else {
        roots.push(map[c.id]);
      }
    });

    return roots.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }, [comments]);

  const renderComment = (cmt, depth = 0) => {
    const fallbackUser =
      (members || []).find((m) => m.userId == cmt.userId) || {};
    const lName = cmt.lastName || fallbackUser.lastName || "";
    const fName = cmt.firstName || fallbackUser.firstName || "";
    const email = cmt.email || fallbackUser.email || "Không có email";

    const displayName = `${`${lName} ${fName}`.trim()} (${email})`.trim();

    const isMyComment = cmt.userId == currentUserId;
    const canComment = assigneeId == currentUserId || hasManageRights;

    return (
      <div
        key={cmt.id}
        style={{ marginLeft: `${depth * 2.5}rem` }}
        className="mt-3"
      >
        <div
          className={`bg-white p-3 rounded shadow-sm border-start border-4 ${
            depth === 0 ? "border-primary" : "border-info"
          }`}
        >
          <div className="d-flex justify-content-between align-items-start mb-2">
            <span className="fw-bold text-primary small">{displayName}</span>
            <span
              className="text-muted small text-end"
              style={{ fontSize: "0.75rem" }}
            >
              {new Date(cmt.createdAt).toLocaleString()}
              {cmt.edited && (
                <span className="ms-1 fst-italic text-warning">(đã sửa)</span>
              )}
            </span>
          </div>

          {editingCommentId === cmt.id ? (
            <div className="mb-2">
              <Form.Control
                as="textarea"
                rows={2}
                value={editContent}
                onChange={(e) => setEditContent(e.target.value)}
                autoFocus
              />
              <div className="mt-2 d-flex justify-content-end gap-2">
                <Button
                  size="sm"
                  style={{
                    backgroundColor: "#6C757D",
                    borderColor: "#6C757D",
                  }}
                  onClick={() => setEditingCommentId(null)}
                >
                  Hủy
                </Button>
                <Button
                  size="sm"
                  style={{
                    backgroundColor: "#28A745",
                    borderColor: "#28A745",
                  }}
                  onClick={() => handleEditComment(cmt.id)}
                >
                  Lưu
                </Button>
              </div>
            </div>
          ) : (
            <div className="text-dark mb-2" style={{ whiteSpace: "pre-wrap" }}>
              {cmt.content}
            </div>
          )}

          <div className="d-flex gap-3 mt-2">
            {canComment && depth < 3 && !editingCommentId && (
              <Button
                variant="link"
                className="p-0 text-decoration-none small"
                style={{ color: "#6C757D" }}
                onClick={() => setReplyTo({ id: cmt.id, name: displayName })}
              >
                <i className="bi bi-reply-fill"></i> Trả lời
              </Button>
            )}

            {isMyComment && !editingCommentId && (
              <Button
                variant="link"
                className="p-0 text-decoration-none small"
                style={{ color: "#6C757D" }}
                onClick={() => {
                  setEditingCommentId(cmt.id);
                  setEditContent(cmt.content);
                  setReplyTo(null);
                }}
              >
                <i className="bi bi-pencil-square"></i> Sửa
              </Button>
            )}

            {(isMyComment || hasManageRights) && !editingCommentId && (
              <Button
                variant="link"
                className="p-0 text-decoration-none small"
                style={{ color: "#FF5733" }}
                onClick={() => handleDeleteComment(cmt.id)}
              >
                <i className="bi bi-trash"></i> Xóa
              </Button>
            )}
          </div>
        </div>

        {cmt.children &&
          cmt.children
            .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
            .map((child) => renderComment(child, depth + 1))}
      </div>
    );
  };

  const canComment = assigneeId == currentUserId || hasManageRights;

  return (
    <>
      <h6 className="fw-bold mb-3 text-secondary mt-4 border-top pt-4">
        <i className="bi bi-chat-left-text me-2"></i>Bình luận
      </h6>

      <div
        className="bg-light p-3 rounded mb-3"
        style={{ maxHeight: "400px", overflowY: "auto" }}
      >
        {commentTree.length > 0 ? (
          commentTree.map((cmt) => renderComment(cmt, 0))
        ) : (
          <div className="text-muted small text-center py-4 fst-italic">
            Chưa có bình luận nào.
          </div>
        )}
      </div>

      {canComment ? (
        <Form onSubmit={handleAddComment} className="mt-2">
          {replyTo && (
            <div className="mb-2 d-flex justify-content-between align-items-center bg-white p-2 rounded">
              <span className="small text-muted fst-italic">
                Đang trả lời:{" "}
                <strong className="text-dark">{replyTo.name}</strong>
              </span>
              <Button
                variant="link"
                size="sm"
                className="p-0 text-decoration-none"
                style={{ color: "#FF5733" }}
                onClick={() => setReplyTo(null)}
              >
                <i className="bi bi-x-circle"></i> Hủy
              </Button>
            </div>
          )}
          <InputGroup>
            <Form.Control
              as="textarea"
              rows={replyTo ? 2 : 1}
              placeholder="Viết bình luận..."
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              autoFocus={!!replyTo}
            />
            <Button
              type="submit"
              style={{ backgroundColor: "#28A745", borderColor: "#28A745" }}
            >
              <i className="bi bi-send"></i> Gửi
            </Button>
          </InputGroup>
        </Form>
      ) : (
        <div
          className="small fst-italic p-2 bg-white border border-danger rounded"
          style={{ color: "#FF5733" }}
        >
          <i className="bi bi-lock-fill me-1"></i>
          Bạn chưa có quyền bình luận.
        </div>
      )}
    </>
  );
};

export default Comment;
