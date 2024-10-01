package JavaL5.chatApp.exception.handler;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiExceptionResponse {
    private HttpStatus status;
    private String errorCode;
}
