package SD.ChatApp.dto.websocket.thread;

import SD.ChatApp.enums.Message_Status;
import SD.ChatApp.enums.Message_Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadMessageReceiving {
    private Long id;
    private String threadId;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private Instant sentTime;
    private Message_Type type;
    private String content;
    private Message_Status messageStatus;
}
