package JavaL5.chatApp.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {

    @NotEmpty
    private String userName;

    @NotEmpty
    private String clientUserId;
}
