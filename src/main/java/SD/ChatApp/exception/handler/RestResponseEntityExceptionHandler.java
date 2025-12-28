package SD.ChatApp.exception.handler;

import SD.ChatApp.exception.conversation.ConversationNotFoundException;
import SD.ChatApp.exception.conversation.GroupNotFoundException;
import SD.ChatApp.exception.conversation.LeaveGroupException;
import SD.ChatApp.exception.conversation.MessageNotFoundException;
import SD.ChatApp.exception.conversation.OneToOneConversationExisted;
import SD.ChatApp.exception.conversation.ThreadAlreadyExistsException;
import SD.ChatApp.exception.conversation.ThreadNotFoundException;
import SD.ChatApp.exception.friend.FriendRelationshipExistedException;
import SD.ChatApp.exception.friend.FriendRelationshipNotFound;
import SD.ChatApp.exception.friend.FriendRequestExistedException;
import SD.ChatApp.exception.request.InvalidRequestException;
import SD.ChatApp.exception.server.*;
import SD.ChatApp.exception.user.NameExistedException;
import SD.ChatApp.exception.user.UserNameExistedException;
import SD.ChatApp.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice()
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_CODE_INTERNAL = "INTERNAL_ERROR";

    private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_TO_HTTP_STATUS_CODE = Map.ofEntries(
            Map.entry(BadCredentialsException.class, HttpStatus.FORBIDDEN),
            Map.entry(UserNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(UserNameExistedException.class, HttpStatus.CONFLICT),
            Map.entry(FriendRelationshipExistedException.class, HttpStatus.CONFLICT),
            Map.entry(FriendRequestExistedException.class, HttpStatus.CONFLICT),
            Map.entry(InvalidRequestException.class, HttpStatus.BAD_REQUEST),
            Map.entry(FriendRelationshipNotFound.class, HttpStatus.CONFLICT),
            Map.entry(NameExistedException.class, HttpStatus.CONFLICT),
            Map.entry(DataAccessException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(LeaveGroupException.class, HttpStatus.CONFLICT),
            Map.entry(ThreadNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(ThreadAlreadyExistsException.class, HttpStatus.CONFLICT),
            Map.entry(MessageNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(ConversationNotFoundException.class, HttpStatus.NOT_FOUND),
            // Server-related exceptions
            Map.entry(ServerNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(ServerMembershipNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(InsufficientPermissionException.class, HttpStatus.FORBIDDEN),
            Map.entry(InvalidServerOperationException.class, HttpStatus.BAD_REQUEST),
            Map.entry(AlreadyMemberException.class, HttpStatus.CONFLICT),
            Map.entry(CannotDeleteDefaultChannelException.class, HttpStatus.BAD_REQUEST)
    );


    private static final Map<Class<? extends RuntimeException>, String> EXCEPTION_TO_ERROR_CODE = Map.ofEntries(
            Map.entry(BadCredentialsException.class, "BAD_CREDENTIALS"),
            Map.entry(UserNotFoundException.class, "USER_NOT_FOUND"),
            Map.entry(UserNameExistedException.class, "USER_NAME_EXISTED"),
            Map.entry(FriendRelationshipExistedException.class, "FRIEND_RELATIONSHIP_EXISTED"),
            Map.entry(FriendRequestExistedException.class, "FRIEND_REQUEST_EXISTED"),
            Map.entry(InvalidRequestException.class, "INVALID_REQUEST_BODY"),
            Map.entry(FriendRelationshipNotFound.class, "FRIEND_RELATIONSHIP_NOT_FOUND"),
            Map.entry(NameExistedException.class, "NAME_EXISTED"),
            Map.entry(DataAccessException.class, "DATABASE_CONFLICT"),
            Map.entry(LeaveGroupException.class, "LEAVE_GROUP_NOT_SUCCESS"),
            Map.entry(ThreadNotFoundException.class, "THREAD_NOT_FOUND"),
            Map.entry(ThreadAlreadyExistsException.class, "THREAD_ALREADY_EXISTS"),
            Map.entry(MessageNotFoundException.class, "MESSAGE_NOT_FOUND"),
            Map.entry(ConversationNotFoundException.class, "CONVERSATION_NOT_FOUND"),
            // Server-related exceptions
            Map.entry(ServerNotFoundException.class, "SERVER_NOT_FOUND"),
            Map.entry(ServerMembershipNotFoundException.class, "SERVER_MEMBERSHIP_NOT_FOUND"),
            Map.entry(InsufficientPermissionException.class, "INSUFFICIENT_PERMISSION"),
            Map.entry(InvalidServerOperationException.class, "INVALID_SERVER_OPERATION"),
            Map.entry(AlreadyMemberException.class, "ALREADY_MEMBER"),
            Map.entry(CannotDeleteDefaultChannelException.class, "CANNOT_DELETE_DEFAULT_CHANNEL")
    );

    @ExceptionHandler()
    ResponseEntity<ApiExceptionResponse> handleException(RuntimeException exception){
        HttpStatus status = EXCEPTION_TO_HTTP_STATUS_CODE.getOrDefault(exception.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR);

        String errorCode = EXCEPTION_TO_ERROR_CODE.getOrDefault(exception.getClass(),
                ERROR_CODE_INTERNAL);
//        log.info("Class: {}, Code: {}", exception.getClass(), errorCode);

        final ApiExceptionResponse response = ApiExceptionResponse.builder().status(status).
                errorCode(errorCode).build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
