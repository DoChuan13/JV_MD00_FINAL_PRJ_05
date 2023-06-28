package backend.repository;

import backend.model.Friend;
import backend.model.enums.FriendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface IFriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findBySentUserIdAndRespUserId(
            Long sentId,
            Long respId);

    @Query(value = "select f from Friend f where (f.sentUser.id=:id or f.respUser.id=:id) and f.status=:status")
    Iterable<Friend> findListFriendByUserIdWithStatus(
            @Param("id") Long id,
            @Param("status") FriendStatus status);

    @Query(value = "select f from Friend f where (f.sentUser.id=:id or f.respUser.id=:id) and f.status=:status")
    Page<Friend> findListFriendByUserIdWithStatus(
            @Param("id") Long id,
            @Param("status") FriendStatus status,
            Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "delete from Friend f where f.respUser.id=:id or f.sentUser.id=:id")
    void deleteByUserId(Long id);

    @Query(value = "select f from Friend f where f.sentUser.id=:id and f.status!='ACCEPT'")
    Iterable<Friend> findSentPendingFriend(
            @Param("id") Long id);

    @Query(value = "select f from Friend f where f.respUser.id=:id and f.status='PENDING'")
    Iterable<Friend> findConfirmPendingFriend(Long id);
}
