package SD.ChatApp.exception.server;

public class InvalidServerOperationException extends RuntimeException {
    public InvalidServerOperationException() {
        super();
    }
    
    public InvalidServerOperationException(String message) {
        super(message);
    }
}

