package SD.ChatApp.exception.server;

public class InsufficientPermissionException extends RuntimeException {
    public InsufficientPermissionException() {
        super();
    }
    
    public InsufficientPermissionException(String message) {
        super(message);
    }
}

