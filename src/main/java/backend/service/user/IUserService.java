package backend.service.user;

import backend.model.User;
import backend.service.IGenericService;

import java.util.Optional;

public interface IUserService extends IGenericService<User> {
    Iterable<User> findByUserAccByName(
            User user,
            String userName);

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    void generateDefaultValueDatabase();

    boolean hasUserRole(User user);

    boolean hasPMRole(User user);

    boolean hasAdminRole(User user);
}
