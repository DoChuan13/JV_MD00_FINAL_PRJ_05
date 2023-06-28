package backend.controller;

import backend.config.Constant;
import backend.dto.request.CommentDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Comment;
import backend.model.Post;
import backend.model.User;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.comment.ICommentService;
import backend.service.post.IPostService;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/comment"})
@CrossOrigin(origins = "*")
public class CommentController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private IPostService postService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICommentService commentService;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @PostMapping
    public ResponseEntity<?> createNewComment(
            @Valid
            @RequestBody
            CommentDTO commentDTO,
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
            responseMessage.setMessage(Constant.COMMENT_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Post> findPost = postService.findById(commentDTO.getPost().getId());
        if (!findPost.isPresent()) {
            responseMessage.setMessage(Constant.POST_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Comment comment = new Comment();
        comment.setPost(findPost.get());
        comment.setComment(commentDTO.getComment());
        comment.setUser(user);

        commentService.save(comment);
        responseMessage.setMessage(Constant.COMMENT_CREATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/{id}"})
    public ResponseEntity<?> editCurrentComment(
            @Valid
            @PathVariable
            Long id,
            @RequestBody CommentDTO commentDTO,
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
            responseMessage.setMessage(Constant.COMMENT_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Comment> findComment = commentService.findById(id);
        if (!findComment.isPresent()) {
            responseMessage.setMessage(Constant.COMMENT_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Comment comment = findComment.get();
        if (!(commentService.checkOwnerComment(comment))) {
            responseMessage.setMessage(Constant.COMMENT_NOT_OWN);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        /*comment.setPost(findPost.get());*/
        comment.setComment(commentDTO.getComment());
        comment.setUser(user);

        commentService.save(comment);
        responseMessage.setMessage(Constant.COMMENT_UPDATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> deleteCurrentComment(
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
        Optional<Comment> findComment = commentService.findById(id);
        if (!findComment.isPresent()) {
            responseMessage.setMessage(Constant.COMMENT_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Comment comment = findComment.get();
        if (!(commentService.checkOwnerComment(comment))) {
            responseMessage.setMessage(Constant.COMMENT_NOT_OWN);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        commentService.deleteById(id);
        responseMessage.setMessage(Constant.COMMENT_DELETE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
