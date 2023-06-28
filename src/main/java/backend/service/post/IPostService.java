package backend.service.post;

import backend.model.Post;
import backend.service.IGenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPostService extends IGenericService<Post> {
    boolean checkOwnerPost(Post post);

    List<Post> findAllOwnPost();

    Page<Post> findAllOwnPost(Pageable pageable);

    List<Post> findAllRelativePost();

    Page<Post> findAllRelativePost(Pageable pageable);

    void deleteByUserId(Long id);

    Iterable<Post> findPostsByUserId(Long id);

    //Image
    void deleteOleImagesPostId(Long id);
}
