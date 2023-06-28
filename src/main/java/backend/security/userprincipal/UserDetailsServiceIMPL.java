package backend.security.userprincipal;

import backend.model.User;
import backend.repository.IUserRepository;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceIMPL implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("User not found"));
        return UserPrinciple.build(user);
    }

    public User getCurrentUser() {
        Optional<User> user;
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        if (userRepository.existsByUserName(userName)) {
            user = userService.findByUserName(userName);
        } else {
            user = Optional.of(new User());
            user.get().setUserName("Anonymous");
        }
        return user.orElse(null);
    }
}
