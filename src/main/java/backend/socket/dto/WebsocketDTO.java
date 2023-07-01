package backend.socket.dto;

import backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WebsocketDTO {
    private Long id;
    private User sentUser;
    private User respUser;
    private String content;
}
