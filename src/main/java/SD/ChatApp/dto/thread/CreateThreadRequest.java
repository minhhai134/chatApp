package SD.ChatApp.dto.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateThreadRequest {
    private Long messageId;
    private String threadName;
}
