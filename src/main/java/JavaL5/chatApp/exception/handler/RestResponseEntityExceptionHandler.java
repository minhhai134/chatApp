package JavaL5.chatApp.exception.handler;

import JavaL5.chatApp.exception.ChannelExistedException;
import JavaL5.chatApp.exception.ChannelNotFoundException;
import JavaL5.chatApp.exception.UserExistedException;
import JavaL5.chatApp.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice()
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String ERROR_CODE_INTERNAL = "INTERNAL_ERROR";

    private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_TO_HTTP_STATUS_CODE = Map.of(
            ChannelNotFoundException.class, HttpStatus.NOT_FOUND,
            ChannelExistedException.class, HttpStatus.CONFLICT,
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            UserExistedException.class, HttpStatus.CONFLICT);

    private static final Map<Class<? extends RuntimeException>, String> EXCEPTION_TO_ERROR_CODE = Map.of(
            ChannelNotFoundException.class, "CHANNEL_NOT_FOUND",
            ChannelExistedException.class, "CHANNEL_EXISTED",
            UserNotFoundException.class, "USER_NOT_FOUND",
            UserExistedException.class, "USER_EXISTED");

    @ExceptionHandler()
    ResponseEntity<ApiExceptionResponse> handleException(RuntimeException exception){
        HttpStatus status = EXCEPTION_TO_HTTP_STATUS_CODE.getOrDefault(exception.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR);

        String errorCode = EXCEPTION_TO_ERROR_CODE.getOrDefault(exception.getClass(),
                ERROR_CODE_INTERNAL);

        final ApiExceptionResponse response = ApiExceptionResponse.builder().status(status).
                errorCode(errorCode).build();
//        log.info("{}", exception.toString());
//        log.info("{} - {}", response.getStatus(), response.getErrorCode());
        return ResponseEntity.status(response.getStatus()).body(response);

    }


}
