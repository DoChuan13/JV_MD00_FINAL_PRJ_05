package backend.service.role;

import backend.model.Role;
import backend.model.enums.RoleName;

import java.util.Optional;

public interface IRoleService {
    Iterable<Role> findAll();

    Optional<Role> findByRoleName(RoleName roleName);
}
