package JavaL5.chatApp.dto.message;

import JavaL5.chatApp.model.Message;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetMessagesResponse {
    private List<Message> messageList;
}
