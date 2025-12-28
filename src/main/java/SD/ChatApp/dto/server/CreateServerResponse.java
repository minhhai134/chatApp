package SD.ChatApp.dto.server;

import SD.ChatApp.model.conversation.Conversation;
import SD.ChatApp.model.server.Server;
import SD.ChatApp.model.server.ServerMembership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServerResponse {
    private Server server;
    private ServerMembership membership;
    private Conversation defaultChannel;
}

