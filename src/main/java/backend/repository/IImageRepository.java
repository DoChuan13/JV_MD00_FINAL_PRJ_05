package backend.repository;

import backend.model.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface IImageRepository extends JpaRepository<PostImage, Long> {
    @Transactional
    @Modifying
    @Query(value = "delete from PostImage pi where pi.post.id=:id")
    void deleteByPostId(
            @Param("id") Long id);
}
