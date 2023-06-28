package backend.service.post;

import backend.model.Post;
import backend.model.User;
import backend.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IPostService extends IGenericService<Post> {
    boolean checkOwnerPost(Post post);

    List<Post> findAllOwnPost();

    Page<Post> findAllOwnPost(Pageable pageable);

    List<Post> findAllRelativePost();

    Page<Post> findAllRelativePost(Pageable pageable);

    void deleteByUserId(Long id);

    Optional<Post> findPostsByCommentId(Long id);

    Iterable<Post> findByLikeUserId(Long id);

    Iterable<Post> findByCommentUserId(Long id);
    void deletePostByUser(User user);
}
