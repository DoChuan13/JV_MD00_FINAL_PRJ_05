package backend.model;

import backend.model.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "friends", uniqueConstraints = @UniqueConstraint(name = "uk_friend_friend", columnNames = {
        "originUserId", "targetUserId"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "originUserId", foreignKey = @ForeignKey(name = "fk_friend_origin_Id"))
    private User sentUser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "targetUserId", foreignKey = @ForeignKey(name = "fk_chat_target_Id"))
    private User respUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default ('PENDING')")
    private FriendStatus status = FriendStatus.PENDING;
}
