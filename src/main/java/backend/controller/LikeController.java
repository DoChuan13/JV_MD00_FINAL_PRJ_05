package backend.controller;

import backend.config.Constant;
import backend.dto.request.LikeDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Like;
import backend.model.Post;
import backend.model.User;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.like.ILikeService;
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
@RequestMapping(value = {"/like"})
@CrossOrigin(origins = "*")
public class LikeController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private IPostService postService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ILikeService likeService;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @PostMapping
    public ResponseEntity<?> likeUnlikePost(
            @Valid
            @RequestBody
            LikeDTO likeDTO,
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
            responseMessage.setMessage(Constant.LIKE_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Post> findPost = postService.findById(likeDTO.getPost().getId());
        if (!findPost.isPresent()) {
            responseMessage.setMessage(Constant.POST_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<Like> findLike = likeService.findByPostIdAndUserId(likeDTO.getPost().getId(), user.getId());
        if (!findLike.isPresent()) {
            Like like = new Like();
            like.setPost(findPost.get());
            like.setUser(user);
            likeService.save(like);

            responseMessage.setMessage(Constant.LIKE_LIKE_SUCCESS);
        } else {
            likeService.deleteById(findLike.get().getId());
            responseMessage.setMessage(Constant.LIKE_UNLIKE_SUCCESS);
        }
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
