package SD.ChatApp.dto.websocket.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadMessageSending {
    private String threadId;
    private String content;
}
