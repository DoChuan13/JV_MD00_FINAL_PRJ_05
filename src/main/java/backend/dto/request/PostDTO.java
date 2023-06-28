package backend.dto.request;

import backend.model.PostImage;
import backend.model.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostDTO {
    @NotBlank
    @NotNull
    private String content;
    //    @NotBlank
    @NotNull
    private PostStatus status;
    @NotNull
    private List<PostImage> images = new ArrayList<>();
}
