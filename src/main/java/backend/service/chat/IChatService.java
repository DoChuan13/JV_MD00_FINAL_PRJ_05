package backend.service.chat;

import backend.model.Chat;
import backend.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IChatService extends IGenericService<Chat> {
    Optional<Chat> findChatBy2UserId(Long id1,Long id2);

    Iterable<Chat> findListChatByUserId(Long id);

    Page<Chat> findPageChatByUserId(
        Long id,
        Pageable pageable);

    void deleteAllChatByUserId(Long id);

    Iterable<Chat> findChatByUserId( Long id);
}
