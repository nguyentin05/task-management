package com.ntt.comment_service.repository;

import com.ntt.comment_service.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByTaskId(String taskId);
    void deleteByTaskId(String taskId);
}
