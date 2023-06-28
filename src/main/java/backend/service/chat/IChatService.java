package backend.service.chat;

import backend.model.Chat;
import backend.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IChatService extends IGenericService<Chat> {
    Iterable<Chat> findBySentUserIdOrRespUserId(
            Long sentId,
            Long respId);

    Iterable<Chat> findListChatByUserId(Long id);

    Optional<Chat> findBySentUserIdAndRespUserId(
            Long sentId,
            Long respId);

    Page<Chat> findPageChatByUserId(
            Long id,
            Pageable pageable);

    void deleteByUserId(Long id);
}
