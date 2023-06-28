package backend.service.comment;

import backend.model.Comment;
import backend.model.User;
import backend.repository.ICommentRepository;
import backend.repository.IPostRepository;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceIMPL implements ICommentService {
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public boolean checkOwnerComment(Comment comment) {
        User user = userDetailsService.getCurrentUser();
        return (user.getId().longValue() == comment.getUser().getId().longValue());
    }

    @Override
    public void deleteByUserId(Long id) {
        commentRepository.deleteByUserId(id);
    }

    @Override
    public Optional<Comment> findCommentByCommentId(Long id) {
        return postRepository.findCommentByCommentId(id);
    }
}
