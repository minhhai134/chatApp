package SD.ChatApp.service.server;

import SD.ChatApp.dto.server.*;
import SD.ChatApp.model.server.Server;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public interface ServerService {

    CreateServerResponse createServer(Principal principal, CreateServerRequest request);

    List<ServerDto> getUserServers(Principal principal);

    ServerDto getServerDetails(Principal principal, String serverId);

    Server updateServer(Principal principal, String serverId, UpdateServerRequest request);

    void deleteServer(Principal principal, String serverId);

    JoinServerResponse joinServer(Principal principal, String serverId);

    void leaveServer(Principal principal, String serverId);

    List<ServerMemberDto> getServerMembers(Principal principal, String serverId);

    void kickMember(Principal principal, String serverId, String userId);

    List<InvitableFriendDto> getInvitableFriends(Principal principal, String serverId);

    void inviteFriendToServer(Principal principal, String serverId, String friendId);
}

