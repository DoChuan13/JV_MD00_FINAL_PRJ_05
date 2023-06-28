package backend.repository;

import backend.model.ChatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface IChatDetailRepository extends JpaRepository<ChatDetail, Long> {
    @Transactional
    void deleteByChatId(Long id);

    Iterable<ChatDetail> findByChatId(Long id);

    @Transactional
    void deleteByUserId(Long id);
}
