package backend.repository;

import backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    Iterable<User> findByNameContaining(String name);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
