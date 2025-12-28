package SD.ChatApp.dto.channel;

import SD.ChatApp.model.conversation.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChannelResponse {
    private Conversation channel;
}

