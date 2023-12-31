package backend.service.chat;

import backend.model.Chat;
import backend.model.ChatDetail;
import backend.repository.IChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatServiceIMPL implements IChatService {
    @Autowired
    private IChatRepository chatRepository;

    @Override
    public Iterable<Chat> findAll() {
        return chatRepository.findAll();
    }

    @Override
    public Page<Chat> findAll(Pageable pageable) {
        return chatRepository.findAll(pageable);
    }

    @Override
    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    @Override
    public Optional<Chat> findById(Long id) {
        return chatRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        chatRepository.deleteById(id);
    }

    @Override
    public Optional<Chat> findChatBy2UserId(
        Long id1,
        Long id2) {
        return chatRepository.findChatBy2UserId(id1, id2);
    }

    @Override
    public Iterable<Chat> findListChatByUserId(Long id) {
        Iterable<Chat> chatList = chatRepository.findListChatByUserId(id);
        List<ChatDetail> chatDetailFilter;
        for (Chat chat : chatList) {
            Date timeCheck;
            if (chat.getSentUser().getId().longValue() == id.longValue()) {
                timeCheck = chat.getSentIn();
            } else {
                timeCheck = chat.getRespIn();
            }
            chatDetailFilter = new ArrayList<>();
            List<ChatDetail> chatDetailList = chat.getChatDetails();
            for (ChatDetail detail : chatDetailList)
                if (detail.getSentTime().compareTo(timeCheck) >= 0) {
                    ChatDetail chatDetail = new ChatDetail();
                    chatDetail.setId(detail.getId());
                    chatDetail.setUser(detail.getUser());
                    chatDetail.setContent(detail.getContent());
                    chatDetail.setSentTime(detail.getSentTime());
                    chatDetailFilter.add(chatDetail);
                }
            ChatDetailComparator detailComparator = new ChatDetailComparator();
            chatDetailFilter.sort(detailComparator);
            chat.setChatDetails(chatDetailFilter);
        }
        /*System.out.println(chatDetailFilter);*/
        return chatList;
    }


    @Override
    public Page<Chat> findPageChatByUserId(
        Long id,
        Pageable pageable) {
        List<Chat> chatList = (List<Chat>) findListChatByUserId(id);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), chatList.size());
        int totalRows = chatList.size();
        return new PageImpl<>(chatList.subList(start, end), pageable, totalRows);
    }

    @Override
    public void deleteAllChatByUserId(Long id) {
        chatRepository.deleteAllChatByUserId(id);
    }

    @Override
    public Iterable<Chat> findChatByUserId(Long id) {
        return chatRepository.findChatByUserId(id);
    }

    private static class ChatDetailComparator implements Comparator<ChatDetail> {

        @Override
        public int compare(ChatDetail o1, ChatDetail o2) {
            return o1.getSentTime().compareTo(o2.getSentTime());
        }
    }
}
