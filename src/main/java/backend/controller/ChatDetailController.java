package backend.controller;

import backend.config.Constant;
import backend.dto.request.ChatDetailDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Chat;
import backend.model.ChatDetail;
import backend.model.User;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.chat.IChatService;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/chat/session"})
@CrossOrigin(origins = "*")
public class ChatDetailController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private IUserService userService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @PostMapping
    public ResponseEntity<?> sendChat(
        @Valid
        @RequestBody
        ChatDetailDTO chatDetailDTO,
        BindingResult result) {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (result.hasErrors()) {
            responseMessage.setMessage(Constant.CHAT_DETAIL_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Chat> findChat = chatService.findById(chatDetailDTO.getChat().getId());
        if (!findChat.isPresent()) {
            responseMessage.setMessage(Constant.CHAT_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Chat currentChat = findChat.get();
        boolean matchSentUsr = currentChat.getSentUser().getId().longValue() == user.getId().longValue();
        boolean matchRespUsr = currentChat.getRespUser().getId().longValue() == user.getId().longValue();
        if (!matchSentUsr && !matchRespUsr) {
            responseMessage.setMessage(Constant.CHAT_NOT_RELATE);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        currentChat.setSentIn(currentChat.getSentIn() == null ? new Date() : currentChat.getSentIn());
        currentChat.setRespIn(currentChat.getRespIn() == null ? new Date() : currentChat.getRespIn());
        currentChat.setLatestTime(new Date());
        ChatDetail chatDetail = new ChatDetail();
        chatDetail.setUser(user);
        chatDetail.setContent(chatDetailDTO.getContent());
        currentChat.getChatDetails().add(chatDetail);
        chatService.save(currentChat);

        responseMessage.setMessage(Constant.CHAT_SEND_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
