package backend.dto.request;

import backend.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDTO {
    @NotNull
    @NotBlank
    private String comment;
    @NotNull
    /*@NotBlank*/
    private Post post;
}
