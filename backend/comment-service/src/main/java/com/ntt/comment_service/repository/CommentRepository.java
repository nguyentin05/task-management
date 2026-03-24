package com.ntt.comment_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ntt.comment_service.domain.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findByTaskId(String taskId, Pageable pageable);

    void deleteByTaskId(String taskId);
}
