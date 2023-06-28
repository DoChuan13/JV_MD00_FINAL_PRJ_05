package backend.repository;

import backend.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface IChatRepository extends JpaRepository<Chat, Long> {
    Iterable<Chat> findBySentUserIdOrRespUserId(
            Long sentId,
            Long respId);

    Optional<Chat> findBySentUserIdAndRespUserId(
            Long sentId,
            Long respId);

    @Query(value = "select c from Chat as c where (c.sentUser.id=:id and c.sentIn!=null ) or (c.respUser.id = :id and c.respIn!=null ) order by c.latestTime desc ")
    Iterable<Chat> findListChatByUserId(Long id);


    @Query(value = "select c from Chat as c where (c.sentUser.id=:id and c.sentIn!=null ) or (c.respUser.id = :id and c.respIn!=null) order by c.latestTime desc ")
    Page<Chat> findPageChatByUserId(
            Long id,
            Pageable pageable);

    @Transactional
    void deleteBySentUserIdOrRespUserId(
            Long sentId,
            Long respId);
}
