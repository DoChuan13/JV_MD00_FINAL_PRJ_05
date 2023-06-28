package backend.model;

import backend.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "fk_post_user_Id"))
    private User user;
    @Column(nullable = false)
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status;
    @Column(nullable = false)
    private Date postTime = new Date();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private List<Comment> comments = new ArrayList<>();
    @OneToMany
    @JoinColumn(name = "postId")
    private List<Like> likes = new ArrayList<>();
    @OneToMany(
            cascade = CascadeType.ALL)
    @JoinColumn(name = "postId")
    private List<PostImage> images = new ArrayList<>();
}
