package backend.dto.request;

import backend.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatDetailDTO {
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    private Chat chat;
}
