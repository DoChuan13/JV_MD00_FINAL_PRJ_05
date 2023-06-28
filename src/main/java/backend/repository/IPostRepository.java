package backend.repository;

import backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserIdOrderByPostTimeDesc(Long id);

    Iterable<Post> findPostsByUserId(Long id);

    @Transactional
    void deleteByUserId(Long id);
}
