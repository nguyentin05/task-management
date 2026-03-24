package com.ntt.comment_service.repository;

import com.ntt.comment_service.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findByTaskId(String taskId, Pageable pageable);

    void deleteByTaskId(String taskId);
}
