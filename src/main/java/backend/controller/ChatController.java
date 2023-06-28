package backend.controller;

import backend.config.Constant;
import backend.dto.request.ChatDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Chat;
import backend.model.Role;
import backend.model.User;
import backend.model.enums.RoleName;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.chat.IChatService;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/chat"})
@CrossOrigin(origins = "*")
public class ChatController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private IUserService userService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @GetMapping(value = {"/{id}"})
    public ResponseEntity<?> getChatDetail(
            @PathVariable Long id) {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Chat> findChat = chatService.findById(id);
        if (!findChat.isPresent()) {
            responseMessage.setMessage(Constant.CHAT_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Chat chat = findChat.get();
        if (chat.getSentUser().getId().longValue() == user.getId().longValue() &&
            chat.getRespUser().getId().longValue() == user.getId().longValue()) {
            responseMessage.setMessage(Constant.CHAT_NOT_RELATE);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    @GetMapping(value = {"/list"})
    public ResponseEntity<?> getListChat() {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Iterable<Chat> listChat = chatService.findListChatByUserId(user.getId());
        return new ResponseEntity<>(listChat, HttpStatus.OK);
    }

    @GetMapping(value = {"/page"})
    public ResponseEntity<?> getPageChat(
            @PageableDefault(size = 5) Pageable pageable) {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Page<Chat> pageChat = chatService.findPageChatByUserId(user.getId(), pageable);
        return new ResponseEntity<>(pageChat, HttpStatus.OK);
    }

    @PostMapping(value = {"/new"})
    public ResponseEntity<?> createNewChat(
            @Valid
            @RequestBody
            ChatDTO chatDTO,
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
            responseMessage.setMessage(Constant.CHAT_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Long targetUserId = chatDTO.getRespUser().getId();
        if (user.getId().longValue() == targetUserId.longValue()) {
            responseMessage.setMessage(Constant.CHAT_CREATE_FAIL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<User> findUser = userService.findById(targetUserId);
        if (!findUser.isPresent()) {
            responseMessage.setMessage(Constant.USER_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        User targetUser = findUser.get();
        Optional<Chat> findChat1 = chatService.findBySentUserIdAndRespUserId(user.getId(), targetUser.getId());
        Optional<Chat> findChat2 = chatService.findBySentUserIdAndRespUserId(targetUser.getId(), user.getId());
        if (findChat1.isPresent() || findChat2.isPresent()) {
            if (findChat1.isPresent()) {
                Chat chat = findChat1.get();
                chat.setSentIn(chat.getSentIn() == null ? (new Date()) : chat.getSentIn());
                chat.setLatestTime(new Date());
                chatService.save(chat);
            }
            if (findChat2.isPresent()) {
                Chat chat = findChat2.get();
                chat.setRespIn(chat.getRespIn() == null ? (new Date()) : chat.getRespIn());
                chat.setLatestTime(new Date());
                chatService.save(chat);
            }
            responseMessage.setMessage(Constant.CHAT_SESSION_EXIST);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        for (Role role : targetUser.getRoles()) {
            if (role.getRoleName() == RoleName.PM || role.getRoleName() == RoleName.ADMIN) {
                responseMessage.setMessage(Constant.CHAT_SEND_AD_FAIL);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
        }
        Chat chat = new Chat();
        chat.setSentUser(user);
        chat.setRespUser(targetUser);
        chat.setSentIn(new Date());
        chatService.save(chat);
        responseMessage.setMessage(Constant.CHAT_CREATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/leave/{id}"})
    public ResponseEntity<?> leaveChatSession(
            @PathVariable Long id) {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Chat> findChat = chatService.findById(id);
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
        } else {
            if (matchSentUsr) {
                currentChat.setSentIn(null);
            }
            if (matchRespUsr) {
                currentChat.setRespIn(null);
            }

        }
        chatService.save(currentChat);
        responseMessage.setMessage(Constant.CHAT_UPDATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
