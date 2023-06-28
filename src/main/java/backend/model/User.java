package backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_userName", columnNames = "userName"),
           @UniqueConstraint(name = "uk_user_email", columnNames = "email")
       })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String name;
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false)
    private String userName;

    /*@NaturalId*/
    @NotBlank
    @Email
    @Size(max = 50)
    @Column(nullable = false)
    private String email;

    @NotBlank
    @JsonIgnore
    @Size(min = 6, max = 100)
    @Column(nullable = false)
    private String password;

    @Lob
    @Column()
    private String avatar
        = "https://firebasestorage.googleapis.com/v0/b/java-full-stack-76e1c.appspot.com/o/avatar.jpeg?alt=media&token=0dd6e72d-fcaf-4565-beec-76c89347767c";

    @Column()
    private Boolean status = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
               joinColumns = @JoinColumn(name = "userId",
                                         foreignKey = @ForeignKey(name = "uk_ur_user_Id")),
               inverseJoinColumns = @JoinColumn(name = "roleId",
                                                foreignKey = @ForeignKey(name = "uk_ur_role_Id")),
               uniqueConstraints = {
                   @UniqueConstraint(name = "uk_user_role", columnNames = {"userId", "roleId"})})
    private Set<Role> roles;
}
