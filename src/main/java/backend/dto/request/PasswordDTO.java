package backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.lang.reflect.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordDTO {
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;

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
