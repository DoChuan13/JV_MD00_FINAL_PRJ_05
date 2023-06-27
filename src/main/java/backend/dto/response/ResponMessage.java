package backend.dto.response;

public class ResponMessage {
    private String message;

    public ResponMessage() {
    }

    public ResponMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
