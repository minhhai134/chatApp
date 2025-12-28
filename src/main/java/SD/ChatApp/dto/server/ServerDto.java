package SD.ChatApp.dto.server;

import SD.ChatApp.enums.ServerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerDto {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String ownerId;
    private String ownerName;
    private ServerRole userRole;
    private long memberCount;
    private long channelCount;
    private Instant createdAt;
    private Instant updatedAt;
}

