package JavaL5.chatApp.dto.message;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageRequest {
    @NotEmpty
    private String message;

    @NotEmpty
    private String clientUserId;
}
