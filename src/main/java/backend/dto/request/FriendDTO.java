package backend.dto.request;

import backend.model.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FriendDTO {
    @NotBlank
    @NotNull
    private FriendStatus status;
}
