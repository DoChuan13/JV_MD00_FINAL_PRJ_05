package backend.repository;

import backend.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IChatRepository extends JpaRepository<Chat, Long> {

    @Query(value = "select c from Chat as c where (c.sentUser.id=:id and c.sentIn!=null ) or (c.respUser.id = :id and c.respIn!=null) order by c.latestTime desc ")
    Page<Chat> findPageChatByUserId(
        Long id,
        Pageable pageable);

    @Query(value = "select c from Chat as c where (c.sentUser.id=:id1 and c.respUser.id=:id2) or (c.respUser.id=:id1 and c.sentUser.id=:id2)")
    Optional<Chat> findChatBy2UserId(
        @Param("id1") Long id1,
        @Param("id2") Long id2);

    @Query(value = "select c from Chat as c where (c.sentUser.id=:id and c.sentIn!=null ) or (c.respUser.id = :id and c.respIn!=null ) order by c.latestTime desc ")
    Iterable<Chat> findListChatByUserId(Long id);

    @Query(value = "delete from Chat c where c.sentUser.id=:id or c.respUser.id=:id")
    void deleteAllChatByUserId(
        @Param("id") Long id);

    @Query(value = "select c from Chat as c where (c.sentUser.id=:id or c.respUser.id=:id)")
    Iterable<Chat> findChatByUserId(
        @Param("id") Long id);
}
