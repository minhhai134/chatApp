package SD.ChatApp.dto.thread;

import SD.ChatApp.model.conversation.Thread;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateThreadResponse {
    private Thread thread;
}
