package SD.ChatApp.dto.thread;

import SD.ChatApp.model.conversation.ThreadMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetThreadMessagesResponse {
    private List<ThreadMessage> messages;
}
