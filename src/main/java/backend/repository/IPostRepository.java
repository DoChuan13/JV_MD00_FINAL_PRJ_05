package backend.repository;

import backend.model.Comment;
import backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserIdOrderByPostTimeDesc(Long id);

    Iterable<Post> findPostsByUserId(Long id);

    @Transactional
    void deleteByUserId(Long id);

    @Query(value = "select p from Post p join p.comments c where c.id=:id")
    Optional<Post> findPostsByCommentId(
        @Param("id") Long id);

    @Query(value = "select c from Post p join p.comments c where c.id=:id")
    Optional<Comment> findCommentByCommentId(
        @Param("id") Long id);

    @Query(value = "select p from Post p join p.likes l where l.user.id=:id")
    Iterable<Post> findByLikeUserId(@Param("id") Long id);

    @Query(value = "select p from Post  p join p.comments c where c.user.id=:id")
    Iterable<Post> findByCommentUserId(@Param("id") Long id);
}
