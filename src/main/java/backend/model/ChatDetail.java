package backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "chat_detail")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    /*(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)*/
    @JoinColumn(name = "chatId", foreignKey = @ForeignKey(name = "fk_cd_chat_Id"))
    private Chat chat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", foreignKey = @ForeignKey(name = "fk_cd_user_Id"))
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Date sentTime = new Date();
}
