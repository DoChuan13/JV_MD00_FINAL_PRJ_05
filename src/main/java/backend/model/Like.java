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
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "postId", foreignKey = @ForeignKey(name = "fk_like_post_Id"))
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "fk_like_user_Id"))
    private User user;
    @Column(nullable = false, columnDefinition = "datetime default (now())")
    private Date likeTime = new Date();
}
