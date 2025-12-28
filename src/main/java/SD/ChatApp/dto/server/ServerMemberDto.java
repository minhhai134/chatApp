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
public class ServerMemberDto {
    private String id;
    private String memberId;
    private String userId;
    private String userName;
    private String avatar;
    private ServerRole role;
    private String nickname;
    private Instant joinedAt;
}

