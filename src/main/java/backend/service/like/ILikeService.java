package backend.service.like;

import backend.model.Like;

import javax.transaction.Transactional;
import java.util.Optional;

public interface ILikeService {
    void save(Like like);

    void deleteById(Long id);

    @Transactional
    void deleteByUserId(Long id);
}
