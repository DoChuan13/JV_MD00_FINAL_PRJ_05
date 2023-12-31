package backend.security.userprincipal;

import backend.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrinciple implements UserDetails {
    private Long id;
    private String name;
    private String userName;
    private String email;
    @JsonIgnore
    private String password;
    private String avatar;
    private Boolean status;
    private Collection<? extends GrantedAuthority> roles;

    public UserPrinciple() {
    }

    public UserPrinciple(
            Long id,
            String name,
            String userName,
            String email,
            String password,
            String avatar,
            Boolean status,
            Collection<? extends GrantedAuthority> roles) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.status = status;
        this.roles = roles;
    }

    public static UserPrinciple build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map((role ->
                                                         new SimpleGrantedAuthority(role.getRoleName().name())))
                                                 .collect(Collectors.toList());
        return new UserPrinciple(user.getId(),
                                 user.getName(),
                                 user.getUserName(),
                                 user.getEmail(),
                                 user.getPassword(),
                                 user.getAvatar(),
                                 user.getStatus(),
                                 authorities);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
