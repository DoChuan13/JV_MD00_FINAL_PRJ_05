package backend.repository;

import backend.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ILikeRepository extends JpaRepository<Like, Long> {
    @Transactional
    void deleteByUserId(Long id);
}
