package backend.controller;

import backend.config.Constant;
import backend.dto.request.FriendDTO;
import backend.dto.request.FriendRequestDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Friend;
import backend.model.Role;
import backend.model.User;
import backend.model.enums.FriendStatus;
import backend.model.enums.RoleName;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.friend.IFriendService;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/friend"})
@CrossOrigin(origins = "*")
public class FriendController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;
    @Autowired
    private IFriendService friendService;
    @Autowired
    private IUserService userService;

    @GetMapping(value = {"/page"})
    public ResponseEntity<?> getPageFriend(
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
        Iterable<Friend> friends = friendService.findListFriendByUserIdWithStatus(user.getId(), FriendStatus.ACCEPT,
                                                                                  pageable);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping(value = {"/list"})
    public ResponseEntity<?> getListFriend(
    ) {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Iterable<Friend> friends = friendService.findListFriendByUserIdWithStatus(user.getId(), FriendStatus.ACCEPT);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping(value = {"/pending"})
    public ResponseEntity<?> getSentPendingFriend() {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(friendService.findSentPendingFriend(user.getId()),
                                    HttpStatus.OK);
    }

    @GetMapping(value = {"/request"})
    public ResponseEntity<?> getConfirmPendingFriend() {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(friendService.findConfirmPendingFriend(user.getId()),
                                    HttpStatus.OK);
    }

    @PostMapping(value = {"/new"})
    public ResponseEntity<?> sentFriendRequest(
            @Valid
            @RequestBody
            FriendRequestDTO requestDTO,
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
            responseMessage.setMessage(Constant.FRIEND_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Long targetUserId = requestDTO.getRespUser().getId();
        if (user.getId().longValue() == targetUserId.longValue()) {
            responseMessage.setMessage(Constant.FRIEND_REQUEST_FAIL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<User> findUser = userService.findById(targetUserId);
        if (!findUser.isPresent()) {
            responseMessage.setMessage(Constant.USER_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        User targetUsr = findUser.get();
        Optional<Friend> findFriend1 = friendService.findBySentUserIdAndRespUserId(user.getId(), targetUserId);
        Optional<Friend> findFriend2 = friendService.findBySentUserIdAndRespUserId(targetUserId, user.getId());
        if (findFriend1.isPresent() || findFriend2.isPresent()) {
            responseMessage.setMessage(Constant.FRIEND_RESEND_FAIL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        for (Role role : targetUsr.getRoles()) {
            if (role.getRoleName() == RoleName.PM || role.getRoleName() == RoleName.ADMIN) {
                responseMessage.setMessage(Constant.FRIEND_SEND_AD_FAIL);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
        }
        Friend friend = new Friend();
        friend.setSentUser(user);
        friend.setRespUser(targetUsr);
        friendService.save(friend);

        responseMessage.setMessage(Constant.Friend_SEND_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/confirm/{id}"})
    public ResponseEntity<?> confirmFriendRequest(
            @Valid
            @PathVariable
            Long id,
            @RequestBody FriendDTO friendDTO,
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
        Optional<Friend> findFriend = friendService.findById(id);
        if (!findFriend.isPresent()) {
            responseMessage.setMessage(Constant.FRIEND_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (result.hasErrors()) {
            responseMessage.setMessage(Constant.FRIEND_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Friend friend = findFriend.get();
        if (friendService.checkOwnerFriendRequest(friend)) {
            responseMessage.setMessage(Constant.FRIEND_CANT_CONFIRM_OWN_REQUEST);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (!(friendService.checkTargetFriendRequest(friend))) {
            responseMessage.setMessage(Constant.FRIEND_NOT_TARGET);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        friend.setStatus(friendDTO.getStatus());
        friendService.save(friend);
        responseMessage.setMessage(Constant.FRIEND_CONFIRM_SUCCESS + " ==> " + friendDTO.getStatus());
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @DeleteMapping(value = {"/delete/{id}"})
    public ResponseEntity<?> deleteFriendRequest(
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
        Optional<Friend> findFriend = friendService.findById(id);
        if (!findFriend.isPresent()) {
            responseMessage.setMessage(Constant.FRIEND_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Friend friend = findFriend.get();
        if (friend.getStatus() != FriendStatus.ACCEPT) {
            if (!friendService.checkOwnerFriendRequest(friend)) {
                responseMessage.setMessage(Constant.FRIEND_NOT_OWN);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
        } else {
            if (!(friendService.checkOwnerFriendRequest(friend)) && !(friendService.checkTargetFriendRequest(friend))) {
                responseMessage.setMessage(Constant.FRIEND_NOT_RELATE);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            }
        }
        friendService.deleteById(id);
        responseMessage.setMessage(Constant.FRIEND_DELETE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
