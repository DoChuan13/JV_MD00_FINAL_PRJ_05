package backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.lang.reflect.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignInDTO {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;

    public boolean checkAllNull() {
        for (Field f : getClass().getDeclaredFields()) {
            try {
                if (f.get(this) != null)
                    return false;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
