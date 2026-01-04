package SD.ChatApp.dto.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitableFriendDto {
    private String friendId;
    private String friendName;
    private String friendAvatar;
}
