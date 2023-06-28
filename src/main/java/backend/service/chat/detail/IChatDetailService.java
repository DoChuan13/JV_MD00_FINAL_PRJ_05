package backend.service.chat.detail;

import backend.model.ChatDetail;

public interface IChatDetailService {
    Iterable<ChatDetail> findByChatId(Long id);

    void save(ChatDetail chatDetail);

    void deleteByUserId(Long id);

    void deleteByChatId(Long id);
}
