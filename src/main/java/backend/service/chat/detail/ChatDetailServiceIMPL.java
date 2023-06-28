package backend.service.chat.detail;

import backend.model.ChatDetail;
import backend.repository.IChatDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatDetailServiceIMPL implements IChatDetailService {
    @Autowired
    private IChatDetailRepository chatDetailRepository;

    @Override
    public Iterable<ChatDetail> findByChatId(Long id) {
        return chatDetailRepository.findByChatId(id);
    }

    @Override
    public void save(ChatDetail chatDetail) {
        chatDetailRepository.save(chatDetail);
    }

    @Override
    public void deleteByUserId(Long id) {
        chatDetailRepository.deleteByUserId(id);
    }

    @Override
    public void deleteByChatId(Long id) {
        chatDetailRepository.deleteByChatId(id);
    }
}
