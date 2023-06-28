package backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "likes",
       uniqueConstraints = @UniqueConstraint(name = "uk_like_p_u", columnNames = {"postId", "userId"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Transient
    private Post post;
    @ManyToOne
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "fk_like_user_Id"))
    private User user;
    @Column(nullable = false)
    private Date likeTime = new Date();
}
