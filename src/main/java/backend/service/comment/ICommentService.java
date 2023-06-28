package backend.service.comment;

import backend.model.Comment;

import java.util.Optional;

public interface ICommentService {
    void save(Comment comment);

    Optional<Comment> findById(Long id);

    void deleteById(Long id);

    boolean checkOwnerComment(Comment comment);

    void deleteByUserId(Long id);

    void deleteByPostId(Long id);
}
