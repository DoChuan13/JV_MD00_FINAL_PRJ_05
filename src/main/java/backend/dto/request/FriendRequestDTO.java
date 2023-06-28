package backend.dto.request;

import backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendRequestDTO {
    private User sentUser;
    @NotNull
    private User respUser;
}
