package backend.service.friend;

import backend.model.Friend;
import backend.model.enums.FriendStatus;
import backend.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IFriendService extends IGenericService<Friend> {
    Optional<Friend> findBySentUserIdAndRespUserId(
            Long sentId,
            Long respId);

    Iterable<Friend> findListFriendByUserIdWithStatus(
            Long sentId,
            FriendStatus status);

    Page<Friend> findListFriendByUserIdWithStatus(
            Long sentId,
            FriendStatus status,
            Pageable pageable);

    boolean checkOwnerFriendRequest(Friend friend);

    boolean checkTargetFriendRequest(Friend friend);

    void deleteByUserId(Long id);

    Iterable<Friend> findSentPendingFriend(Long id);

    Iterable<Friend> findConfirmPendingFriend(Long id);
}
