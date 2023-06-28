package backend.service.like;

import backend.model.Like;
import backend.repository.ILikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceIMPL implements ILikeService {
    @Autowired
    private ILikeRepository likeRepository;

    @Override
    public void save(Like like) {
        likeRepository.save(like);
    }

    @Override
    public void deleteById(Long id) {
        likeRepository.deleteById(id);
    }

    @Override
    public void deleteByUserId(Long id) {
        likeRepository.deleteByUserId(id);
    }
}
