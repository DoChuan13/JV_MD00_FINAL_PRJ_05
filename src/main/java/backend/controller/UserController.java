package backend.controller;

import backend.config.Constant;
import backend.dto.request.PasswordDTO;
import backend.dto.request.UserDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Chat;
import backend.model.Post;
import backend.model.Role;
import backend.model.User;
import backend.model.enums.RoleName;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.chat.IChatService;
import backend.service.chat.detail.IChatDetailService;
import backend.service.comment.ICommentService;
import backend.service.friend.IFriendService;
import backend.service.like.ILikeService;
import backend.service.post.IPostService;
import backend.service.role.IRoleService;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(origins = "*")
public class UserController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IPostService postService;
    @Autowired
    private ICommentService commentService;
    @Autowired
    private ILikeService likeService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IChatDetailService chatDetailService;
    @Autowired
    private IFriendService friendService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getCurrentUser() {
        User user = userDetailsService.getCurrentUser();
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = {"/page"})
    public ResponseEntity<?> getPageUser(
        @PageableDefault() Pageable pageable) {
        User user = userDetailsService.getCurrentUser();
        if (userService.hasUserRole(user)) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(userService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping(value = {"/list"})
    public ResponseEntity<?> getListUser() {
        User user = userDetailsService.getCurrentUser();
        if (userService.hasUserRole(user)) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }


    @GetMapping(value = {"/find"})
    public ResponseEntity<?> findByUserAccByName(
        @RequestParam(name = "name") String name) {
        User user = userDetailsService.getCurrentUser();
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(userService.findByUserAccByName(user, name), HttpStatus.OK);
    }

    @PutMapping(value = {"/edit/info/{id}"})
    public ResponseEntity<?> editUserInfo(
        @PathVariable Long id,
        @RequestBody UserDTO userDTO) {
        User user = userDetailsService.getCurrentUser();
        if (user.getId().longValue() != id.longValue()) {
            responseMessage.setMessage(Constant.USER_ACCESS_FAIL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (userDTO.checkAllNull()) {
            responseMessage.setMessage(Constant.USER_FORM_INVALID);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        boolean nullName = userDTO.getName() == null;
        boolean checkName = user.getName().equalsIgnoreCase(userDTO.getName());
        boolean nullUserName = userDTO.getUserName() == null;
        boolean checkUserName = user.getUserName().equalsIgnoreCase(userDTO.getUserName());
        boolean nullEmail = userDTO.getEmail() == null;
        boolean checkEmail = user.getEmail().equalsIgnoreCase(userDTO.getEmail());
        boolean nullAvatar = userDTO.getAvatar() == null;
        boolean checkAvatar = user.getAvatar().equalsIgnoreCase(userDTO.getAvatar());
        if ((nullName && nullUserName && nullEmail && nullAvatar) ||
            (checkName && checkUserName && checkEmail && checkAvatar)) {
            responseMessage.setMessage(Constant.USER_NO_CHANGE);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (!checkName && !nullName) {
            user.setName(userDTO.getName());
        }
        if (!checkUserName && !nullUserName) {
            if (userService.existsByUserName(userDTO.getUserName())) {
                responseMessage.setMessage(Constant.USER_NAME_EXIST);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            } else {
                user.setUserName(userDTO.getUserName());
            }
        }
        if (!checkEmail && !nullEmail) {
            if (userService.existsByEmail(userDTO.getEmail())) {
                responseMessage.setMessage(Constant.USER_EMAIL_EXIST);
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            } else {
                user.setEmail(userDTO.getEmail());
            }
        }
        if (!checkAvatar && !nullAvatar) {
            user.setAvatar(userDTO.getAvatar());
        }

        userService.save(user);
        responseMessage.setMessage(Constant.USER_INFO_UPDATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/edit/password/{id}"})
    public ResponseEntity<?> editUserPassword(
        @Valid
        @PathVariable Long id,
        @RequestBody PasswordDTO passwordDTO,
        BindingResult result) {
        User user = userDetailsService.getCurrentUser();
        if (result.hasErrors()) {
            responseMessage.setMessage(Constant.USER_PASSWORD_FORM_INVALID);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getId().longValue() != id.longValue()) {
            responseMessage.setMessage(Constant.USER_ACCESS_FAIL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (passwordDTO.checkAllNull()) {
            responseMessage.setMessage(Constant.USER_FORM_INVALID);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            responseMessage.setMessage(Constant.USER_OLD_PASSWORD_NOT_MATCH);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        String newPassword = passwordDTO.getNewPassword();
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            responseMessage.setMessage(Constant.USER_PASSWORD_INVALID);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        user.setPassword(passwordEncoder.encode(newPassword));

        userService.save(user);
        responseMessage.setMessage(Constant.USER_PASSWORD_UPDATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/edit/role/{id}"})
    public ResponseEntity<?> changeRoleAcc(
        @PathVariable Long id,
        @RequestBody UserDTO userDTO) {
        User user = userDetailsService.getCurrentUser();
        if (!userService.hasAdminRole(user)) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<User> findUser = userService.findById(id);
        if (!findUser.isPresent()) {
            responseMessage.setMessage(Constant.USER_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        User targetUser = findUser.get();
        if (userService.hasAdminRole(targetUser)) {
            responseMessage.setMessage(Constant.USER_DENY_ADMIN_ACC);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Set<String> newRoles = userDTO.getRoles();
        if (userDTO.checkAllNull()) {
            responseMessage.setMessage(Constant.USER_FORM_INVALID);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        boolean nullRole = newRoles == null;
        if (nullRole || newRoles.size() == 0) {
            responseMessage.setMessage(Constant.USER_ROLE_INVALID);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        }
        Set<Role> roles = new HashSet<>();
        AtomicBoolean adminPermission = new AtomicBoolean(false);
        newRoles.forEach(role -> {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    Role adminRole = roleService.findByRoleName(RoleName.ADMIN).orElseThrow(
                        () -> new RuntimeException(Constant.ROLE_NOT_FOUND));
                    roles.add(adminRole);
                    break;
                case "PM":
                    Role pmRole = roleService.findByRoleName(RoleName.PM).orElseThrow(
                        () -> new RuntimeException(Constant.ROLE_NOT_FOUND));
                    roles.add(pmRole);
                    break;
                default:
                    Role userRole = roleService.findByRoleName(RoleName.USER).orElseThrow(
                        () -> new RuntimeException(Constant.ROLE_NOT_FOUND));
                    roles.add(userRole);
            }
        });
        if (adminPermission.get()) {
            responseMessage.setMessage(Constant.USER_ADMIN_UNIQUE);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        targetUser.setRoles(roles);

        userService.save(targetUser);
        responseMessage.setMessage(Constant.USER_CHANGE_ROLE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/edit/block/{id}"})
    public ResponseEntity<?> blockUnblockAcc(
        @PathVariable Long id) {
        User user = userDetailsService.getCurrentUser();
        if (userService.hasUserRole(user)) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<User> findUser = userService.findById(id);
        if (!findUser.isPresent()) {
            responseMessage.setMessage(Constant.USER_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getId().longValue() == id.longValue()) {
            responseMessage.setMessage(Constant.USER_CANT_BLOCK_SELF);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        User targetUser = findUser.get();
        if (userService.hasAdminRole(targetUser)) {
            responseMessage.setMessage(Constant.USER_DENY_BLOCK_ADMIN);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (userService.hasPMRole(user) && userService.hasPMRole(targetUser)) {
            responseMessage.setMessage(Constant.USER_DENY_BLOCK_PM);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (targetUser.getStatus()) {
            responseMessage.setMessage(Constant.USER_UNBLOCK_SUCCESS);
        } else {
            responseMessage.setMessage(Constant.USER_BLOCK_SUCCESS);
        }
        targetUser.setStatus(!targetUser.getStatus());
        userService.save(targetUser);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @DeleteMapping(value = {"/delete/{id}"})
    public ResponseEntity<?> deleteAcc(
        @PathVariable Long id) {
        User user = userDetailsService.getCurrentUser();
        if (!userService.hasAdminRole(user)) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<User> findUser = userService.findById(id);
        if (!findUser.isPresent()) {
            responseMessage.setMessage(Constant.USER_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getId().longValue() == id.longValue()) {
            responseMessage.setMessage(Constant.USER_CANT_DELETE_SELF);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        User targetUser = findUser.get();
        if (userService.hasAdminRole(targetUser)) {
            responseMessage.setMessage(Constant.USER_DENY_DELETE_ADMIN);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        List<Chat> chats = (List<Chat>) chatService.findBySentUserIdOrRespUserId(id, id);
        for (Chat chat : chats) {
            Long chatId = chat.getId();
            chatDetailService.deleteByChatId(chatId);
        }
        chatDetailService.deleteByUserId(id);
        chatService.deleteByUserId(id);
        List<Post> posts = (List<Post>) postService.findPostsByUserId(id);
        for (Post post : posts) {
            Long postId = post.getId();
            commentService.deleteByPostId(postId);
            likeService.deleteByPostId(postId);
        }
        likeService.deleteByUserId(id);
        commentService.deleteByUserId(id);
        postService.deleteByUserId(id);
        friendService.deleteByUserId(id);
        userService.deleteById(id);
        responseMessage.setMessage(Constant.USER_DELETE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
