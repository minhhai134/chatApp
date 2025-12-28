package SD.ChatApp.service.server;

import SD.ChatApp.dto.server.*;
import SD.ChatApp.enums.ServerRole;
import SD.ChatApp.exception.server.*;
import SD.ChatApp.exception.user.UserNotFoundException;
import SD.ChatApp.model.User;
import SD.ChatApp.model.conversation.Conversation;
import SD.ChatApp.model.server.Server;
import SD.ChatApp.model.server.ServerMembership;
import SD.ChatApp.repository.UserRepository;
import SD.ChatApp.repository.conversation.ConversationRepository;
import SD.ChatApp.repository.server.ServerMembershipRepository;
import SD.ChatApp.repository.server.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMembershipRepository serverMembershipRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public CreateServerResponse createServer(Principal principal, CreateServerRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Create server
        Server server = Server.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .ownerId(user.getId())
                .build();
        server = serverRepository.save(server);

        // Create owner membership
        ServerMembership membership = ServerMembership.builder()
                .serverId(server.getId())
                .userId(user.getId())
                .role(ServerRole.OWNER)
                .build();
        membership = serverMembershipRepository.save(membership);

        // Create default channel
        Conversation defaultChannel = Conversation.builder()
                .serverId(server.getId())
                .channelName("general")
                .channelDescription("General discussion")
                .position(0)
                .isDefault(true)
                .lastActive(Instant.now())
                .build();
        defaultChannel = conversationRepository.save(defaultChannel);

        log.info("Server '{}' created by user '{}'", server.getName(), user.getName());

        return CreateServerResponse.builder()
                .server(server)
                .membership(membership)
                .defaultChannel(defaultChannel)
                .build();
    }

    @Override
    public List<ServerDto> getUserServers(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        List<Server> servers = serverRepository.findServersByUserId(user.getId());

        return servers.stream().map(server -> {
            ServerMembership membership = serverMembershipRepository
                    .findByServerIdAndUserId(server.getId(), user.getId())
                    .orElse(null);

            User owner = userRepository.findById(server.getOwnerId()).orElse(null);

            return ServerDto.builder()
                    .id(server.getId())
                    .name(server.getName())
                    .description(server.getDescription())
                    .icon(server.getIcon())
                    .ownerId(server.getOwnerId())
                    .ownerName(owner != null ? owner.getName() : null)
                    .userRole(membership != null ? membership.getRole() : null)
                    .memberCount(serverMembershipRepository.countByServerId(server.getId()))
                    .channelCount(conversationRepository.countByServerId(server.getId()))
                    .createdAt(server.getCreatedAt())
                    .updatedAt(server.getUpdatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public ServerDto getServerDetails(Principal principal, String serverId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        // Verify user is a member
        ServerMembership membership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, user.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        User owner = userRepository.findById(server.getOwnerId()).orElse(null);

        return ServerDto.builder()
                .id(server.getId())
                .name(server.getName())
                .description(server.getDescription())
                .icon(server.getIcon())
                .ownerId(server.getOwnerId())
                .ownerName(owner != null ? owner.getName() : null)
                .userRole(membership.getRole())
                .memberCount(serverMembershipRepository.countByServerId(serverId))
                .channelCount(conversationRepository.countByServerId(serverId))
                .createdAt(server.getCreatedAt())
                .updatedAt(server.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public Server updateServer(Principal principal, String serverId, UpdateServerRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        // Only owner or admin can update
        ServerMembership membership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, user.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        if (membership.getRole() == ServerRole.MEMBER) {
            throw new InsufficientPermissionException("Only admin or owner can update server");
        }

        if (request.getName() != null) {
            server.setName(request.getName());
        }
        if (request.getDescription() != null) {
            server.setDescription(request.getDescription());
        }
        if (request.getIcon() != null) {
            server.setIcon(request.getIcon());
        }

        return serverRepository.save(server);
    }

    @Override
    @Transactional
    public void deleteServer(Principal principal, String serverId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        // Only owner can delete
        if (!server.getOwnerId().equals(user.getId())) {
            throw new InsufficientPermissionException("Only server owner can delete the server");
        }

        // Notify all members before deletion
        List<ServerMembership> members = serverMembershipRepository.findByServerId(serverId);
        for (ServerMembership member : members) {
            if (!member.getUserId().equals(user.getId())) {
                User memberUser = userRepository.findById(member.getUserId()).orElse(null);
                if (memberUser != null) {
                    messagingTemplate.convertAndSendToUser(
                            memberUser.getUsername(), "/queue/messages",
                            java.util.Map.of(
                                    "notificationType", "SERVER_DELETED",
                                    "serverId", serverId,
                                    "serverName", server.getName()
                            ));
                }
            }
        }

        // CASCADE will handle memberships and channels
        serverRepository.delete(server);
        log.info("Server '{}' deleted by owner '{}'", server.getName(), user.getName());
    }

    @Override
    @Transactional
    public JoinServerResponse joinServer(Principal principal, String serverId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        // Check if already a member
        if (serverMembershipRepository.existsByServerIdAndUserId(serverId, user.getId())) {
            throw new AlreadyMemberException();
        }

        ServerMembership membership = ServerMembership.builder()
                .serverId(serverId)
                .userId(user.getId())
                .role(ServerRole.MEMBER)
                .build();
        membership = serverMembershipRepository.save(membership);

        log.info("User '{}' joined server '{}'", user.getName(), server.getName());

        return JoinServerResponse.builder()
                .server(server)
                .membership(membership)
                .build();
    }

    @Override
    @Transactional
    public void leaveServer(Principal principal, String serverId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        ServerMembership membership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, user.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        // Owner cannot leave without transferring ownership
        if (server.getOwnerId().equals(user.getId())) {
            long memberCount = serverMembershipRepository.countByServerId(serverId);
            if (memberCount == 1) {
                // Only owner left - delete the server
                deleteServer(principal, serverId);
                return;
            } else {
                throw new InvalidServerOperationException(
                        "Owner must transfer ownership before leaving");
            }
        }

        serverMembershipRepository.delete(membership);
        log.info("User '{}' left server '{}'", user.getName(), server.getName());
    }

    @Override
    public List<ServerMemberDto> getServerMembers(Principal principal, String serverId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Verify user is a member
        if (!serverMembershipRepository.existsByServerIdAndUserId(serverId, user.getId())) {
            throw new ServerMembershipNotFoundException();
        }

        List<ServerMembership> memberships = serverMembershipRepository.findByServerId(serverId);

        return memberships.stream().map(membership -> {
            User member = userRepository.findById(membership.getUserId()).orElse(null);
            return ServerMemberDto.builder()
                    .id(membership.getId())
                    .memberId(membership.getId())
                    .userId(membership.getUserId())
                    .userName(member != null ? member.getName() : null)
                    .avatar(member != null ? member.getAvatar() : null)
                    .role(membership.getRole())
                    .nickname(membership.getNickname())
                    .joinedAt(membership.getJoinedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void kickMember(Principal principal, String serverId, String userId) {
        User admin = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        Server server = serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        // Check permission
        ServerMembership adminMembership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, admin.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        if (adminMembership.getRole() == ServerRole.MEMBER) {
            throw new InsufficientPermissionException("Only admin or owner can kick members");
        }

        // Cannot kick owner
        if (server.getOwnerId().equals(userId)) {
            throw new InvalidServerOperationException("Cannot kick the server owner");
        }

        // Cannot kick self
        if (admin.getId().equals(userId)) {
            throw new InvalidServerOperationException("Cannot kick yourself");
        }

        ServerMembership targetMembership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, userId)
                .orElseThrow(ServerMembershipNotFoundException::new);

        // Admin cannot kick another admin (only owner can)
        if (targetMembership.getRole() == ServerRole.ADMIN
                && adminMembership.getRole() != ServerRole.OWNER) {
            throw new InsufficientPermissionException("Only owner can kick admins");
        }

        User kickedUser = userRepository.findById(userId).orElse(null);
        serverMembershipRepository.delete(targetMembership);

        // Notify kicked user
        if (kickedUser != null) {
            messagingTemplate.convertAndSendToUser(
                    kickedUser.getUsername(), "/queue/messages",
                    java.util.Map.of(
                            "notificationType", "KICKED_FROM_SERVER",
                            "serverId", serverId,
                            "serverName", server.getName()
                    ));
        }

        log.info("User '{}' kicked from server '{}' by '{}'",
                kickedUser != null ? kickedUser.getName() : userId,
                server.getName(), admin.getName());
    }
}

