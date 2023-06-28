package backend.service.friend;

import backend.model.Friend;
import backend.model.User;
import backend.model.enums.FriendStatus;
import backend.repository.IFriendRepository;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FriendServiceIMPL implements IFriendService {
    @Autowired
    private IFriendRepository friendRepository;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @Override
    public Iterable<Friend> findAll() {
        return friendRepository.findAll();
    }

    @Override
    public Page<Friend> findAll(Pageable pageable) {
        return friendRepository.findAll(pageable);
    }

    @Override
    public void save(Friend friend) {
        friendRepository.save(friend);
    }

    @Override
    public Optional<Friend> findById(Long id) {
        return friendRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        friendRepository.deleteById(id);
    }

    @Override
    public Optional<Friend> findBySentUserIdAndRespUserId(
            Long sentId,
            Long respId) {
        return friendRepository.findBySentUserIdAndRespUserId(sentId, respId);
    }

    @Override
    public Iterable<Friend> findListFriendByUserIdWithStatus(
            Long sentId,
            FriendStatus status) {
        return friendRepository.findListFriendByUserIdWithStatus(sentId, status);
    }

    @Override
    public Page<Friend> findListFriendByUserIdWithStatus(
            Long sentId,
            FriendStatus status,
            Pageable pageable) {
        return friendRepository.findListFriendByUserIdWithStatus(sentId, status, pageable);
    }

    @Override
    public boolean checkOwnerFriendRequest(Friend friend) {
        User user = userDetailsService.getCurrentUser();
        return (user.getId().longValue() == friend.getSentUser().getId().longValue());
    }

    @Override
    public boolean checkTargetFriendRequest(Friend friend) {
        User user = userDetailsService.getCurrentUser();
        return (user.getId().longValue() == friend.getRespUser().getId().longValue());
    }

    @Override
    public void deleteByUserId(Long id) {
        friendRepository.deleteByUserId(id);
    }

    @Override
    public Iterable<Friend> findSentPendingFriend(Long id) {
        return friendRepository.findSentPendingFriend(id);
    }

    @Override
    public Iterable<Friend> findConfirmPendingFriend(
            Long id) {
        return friendRepository.findConfirmPendingFriend(id);
    }
}
