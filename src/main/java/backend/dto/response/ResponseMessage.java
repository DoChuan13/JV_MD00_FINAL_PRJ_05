package backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseMessage {
    private static ResponseMessage instance;
    private String message;

    public static ResponseMessage getInstance() {
        if (instance == null) instance = new ResponseMessage();
        return instance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
