package backend.repository;

import backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    void deleteByUserId(Long id);
}
