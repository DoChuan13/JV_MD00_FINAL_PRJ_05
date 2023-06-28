package backend.controller;

import backend.config.Constant;
import backend.dto.request.PostDTO;
import backend.dto.response.ResponseMessage;
import backend.model.Post;
import backend.model.User;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.post.IPostService;
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
import java.util.Optional;

@RestController
@RequestMapping(value = {"/post"})
@CrossOrigin(origins = "*")
public class PostController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    @Autowired
    private IPostService postService;
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @GetMapping(value = "/page")
    public ResponseEntity<?> getPagePost(
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
        Page<Post> posts = postService.findAllRelativePost(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping(value = "/home/page")
    public ResponseEntity<?> getPageOwnPost(
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
        Page<Post> posts = postService.findAllOwnPost(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<?> getListPost() {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Iterable<Post> posts = postService.findAllRelativePost();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping(value = "/home/list")
    public ResponseEntity<?> getListOwnPost() {
        User user = userDetailsService.getCurrentUser();
        if (!(userService.hasUserRole(user))) {
            responseMessage.setMessage(Constant.DENY_PERMISSION);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (user.getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Iterable<Post> posts = postService.findAllOwnPost();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDetailPost(
        @PathVariable Long id) {
        return new ResponseEntity<>(postService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createNewPost(
        @Valid
        @RequestBody
        PostDTO postDTO,
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
            responseMessage.setMessage(Constant.POST_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setStatus(postDTO.getStatus());
        post.setImages(postDTO.getImages());
        post.setUser(user);

        postService.save(post);

        responseMessage.setMessage(Constant.POST_CREATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping(value = {"/{id}"})
    public ResponseEntity<?> editCurrentPost(
        @Valid
        @PathVariable
        Long id,
        @RequestBody PostDTO postDTO,
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
        Optional<Post> findPost = postService.findById(id);
        if (!findPost.isPresent()) {
            responseMessage.setMessage(Constant.POST_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (result.hasErrors()) {
            responseMessage.setMessage(Constant.POST_FORM_INVALID);
            /*System.out.println(result.getFieldError());*/
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Post currentPost = findPost.get();
        if (!(postService.checkOwnerPost(currentPost))) {
            responseMessage.setMessage(Constant.POST_NOT_OWN);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        currentPost.setContent(postDTO.getContent());
        currentPost.setStatus(postDTO.getStatus());
        currentPost.setImages(postDTO.getImages());

        postService.save(currentPost);

        responseMessage.setMessage(Constant.POST_UPDATE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> deleteCurrentPost(
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
        Optional<Post> findPost = postService.findById(id);
        if (!findPost.isPresent()) {
            responseMessage.setMessage(Constant.POST_NOT_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (!(postService.checkOwnerPost(findPost.get()))) {
            responseMessage.setMessage(Constant.POST_NOT_OWN);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        postService.deleteById(id);
        responseMessage.setMessage(Constant.POST_DELETE_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
}
