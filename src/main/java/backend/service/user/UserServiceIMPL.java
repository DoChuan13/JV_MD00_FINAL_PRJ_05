package backend.service.user;

import backend.model.Role;
import backend.model.User;
import backend.model.enums.RoleName;
import backend.repository.IRoleRepository;
import backend.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceIMPL implements IUserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public Iterable<User> findByUserAccByName(
            User currentUser,
            String name) {
        List<User> users = (List<User>) userRepository.findByNameContaining(name);
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (currentUser.getId().longValue() == user.getId().longValue()) {
                continue;
            }
            if (hasAdminRole(user) || hasPMRole(user)) {
                continue;
            }
            result.add(user);
        }
        return result;
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void generateDefaultValueDatabase() {
        Iterable<Role> roles = roleRepository.findAll();
        long roleSize = roles.spliterator().getExactSizeIfKnown();
        if (roleSize == 0) {
            //Generate Role Info
            List<Role> roleList = new ArrayList<>();
            roleList.add(new Role(1, RoleName.ADMIN));
            roleList.add(new Role(2, RoleName.PM));
            roleList.add(new Role(3, RoleName.USER));
            roleRepository.saveAll(roleList);

            //Generate Admin Info
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Set<Role> adminRole = new HashSet<>();
            adminRole.add(new Role(1, RoleName.ADMIN));
            User admin = new User();
            admin.setName("admin");
            admin.setUserName("admin");
            admin.setEmail("admin@admin.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRoles(adminRole);
            userRepository.save(admin);
        }
    }

    @Override
    public boolean hasUserRole(User user) {
        for (Role role : user.getRoles()) {
            if (role.getRoleName() == RoleName.USER) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPMRole(User user) {
        for (Role role : user.getRoles()) {
            if (role.getRoleName() == RoleName.PM) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAdminRole(User user) {
        for (Role role : user.getRoles()) {
            if (role.getRoleName() == RoleName.ADMIN) {
                return true;
            }
        }
        return false;
    }
}
