package backend.service.post;

import backend.model.Friend;
import backend.model.Post;
import backend.model.User;
import backend.model.enums.FriendStatus;
import backend.model.enums.PostStatus;
import backend.repository.IImageRepository;
import backend.repository.IPostRepository;
import backend.security.userprincipal.UserDetailsServiceIMPL;
import backend.service.friend.IFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceIMPL implements IPostService {
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private UserDetailsServiceIMPL userDetailsService;
    @Autowired
    private IFriendService friendService;
    @Autowired
    private IImageRepository imageRepository;

    @Override
    public Iterable<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public boolean checkOwnerPost(Post post) {
        User user = userDetailsService.getCurrentUser();
        return (user.getId().longValue() == post.getUser().getId().longValue());
    }

    @Override
    public List<Post> findAllOwnPost() {
        User user = userDetailsService.getCurrentUser();
        return postRepository.findAllByUserIdOrderByPostTimeDesc(user.getId());
    }

    @Override
    public Page<Post> findAllOwnPost(Pageable pageable) {
        List<Post> postList = findAllOwnPost();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postList.size());
        int totalRows = postList.size();
        return new PageImpl<>(postList.subList(start, end), pageable, totalRows);
    }

    @Override
    public List<Post> findAllRelativePost() {
        User user = userDetailsService.getCurrentUser();
        List<Post> result;
        result = postRepository.findAllByUserIdOrderByPostTimeDesc(user.getId());
        List<Friend> friends = (List<Friend>) friendService.findListFriendByUserIdWithStatus(user.getId(),
                                                                                             FriendStatus.ACCEPT);
        for (Friend friend : friends) {
            Long sentId = friend.getSentUser().getId();
            Long respId = friend.getRespUser().getId();
            List<Post> findPost;
            if (sentId.longValue() != user.getId().longValue()) {
                findPost = postRepository.findAllByUserIdOrderByPostTimeDesc(sentId);
            } else {
                findPost = postRepository.findAllByUserIdOrderByPostTimeDesc(respId);
            }
            for (Post post : findPost) {
                if (post.getStatus() != PostStatus.PRIVATE) {
                    result.add(post);
                }
            }
        }
        ComparatorPost comparatorPost = new ComparatorPost();
        result.sort(comparatorPost);
        return result;
    }

    @Override
    public Page<Post> findAllRelativePost(Pageable pageable) {
        List<Post> postList = findAllRelativePost();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), postList.size());
        int totalRows = postList.size();
        return new PageImpl<>(postList.subList(start, end), pageable, totalRows);
    }

    @Override
    public void deleteByUserId(Long id) {
        postRepository.deleteByUserId(id);
    }

    @Override
    public Iterable<Post> findPostsByUserId(Long id) {
        return postRepository.findPostsByUserId(id);
    }

    @Override
    public void deleteOleImagesPostId(Long id) {
        imageRepository.deleteByPostId(id);
    }

    private static class ComparatorPost implements Comparator<Post> {
        @Override
        public int compare(
                Post o1,
                Post o2) {
            return -(o1.getPostTime().compareTo(o2.getPostTime()));
        }
    }
}
