package SD.ChatApp.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDto {
    private String id;
    private String serverId;
    private String channelName;
    private String channelDescription;
    private Integer position;
    private Boolean isDefault;
    private Long lastMessageID;
    private String lastMessageContent;
    private Instant lastActive;
}

