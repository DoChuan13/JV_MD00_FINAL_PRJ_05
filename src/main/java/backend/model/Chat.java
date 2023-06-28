package backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "chats")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sentUserId", foreignKey = @ForeignKey(name = "fk_chat_sent_Id"))
    private User sentUser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "respUserId", foreignKey = @ForeignKey(name = "fk_chat_resp_Id"))
    private User respUser;

    @Column(name = "sentIn")
    private Date sentIn;
    @Column(name = "respIn")
    private Date respIn;
    @Column(name = "createTime", nullable = false)
    private Date createTime = new Date();
    @Column(name = "latestTime", nullable = false)
    private Date latestTime = new Date();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "chatId")
    private List<ChatDetail> chatDetails = new ArrayList<>();
}
