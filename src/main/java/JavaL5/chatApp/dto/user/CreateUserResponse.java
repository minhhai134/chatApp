package JavaL5.chatApp.dto.user;

import JavaL5.chatApp.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {
    private User user;
}
