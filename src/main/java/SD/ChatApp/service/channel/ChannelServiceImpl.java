package SD.ChatApp.service.channel;

import SD.ChatApp.dto.channel.*;
import SD.ChatApp.enums.ServerRole;
import SD.ChatApp.exception.conversation.ConversationNotFoundException;
import SD.ChatApp.exception.server.*;
import SD.ChatApp.exception.user.UserNotFoundException;
import SD.ChatApp.model.User;
import SD.ChatApp.model.conversation.Conversation;
import SD.ChatApp.model.server.ServerMembership;
import SD.ChatApp.repository.UserRepository;
import SD.ChatApp.repository.conversation.ConversationRepository;
import SD.ChatApp.repository.server.ServerMembershipRepository;
import SD.ChatApp.repository.server.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelServiceImpl implements ChannelService {

    private final ServerRepository serverRepository;
    private final ServerMembershipRepository serverMembershipRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CreateChannelResponse createChannel(Principal principal, String serverId, CreateChannelRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Verify server exists
        serverRepository.findById(serverId)
                .orElseThrow(ServerNotFoundException::new);

        // Check permission
        ServerMembership membership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, user.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        if (membership.getRole() == ServerRole.MEMBER) {
            throw new InsufficientPermissionException("Only admin or owner can create channels");
        }

        // Get next position
        long channelCount = conversationRepository.countByServerId(serverId);

        Conversation channel = Conversation.builder()
                .serverId(serverId)
                .channelName(request.getChannelName())
                .channelDescription(request.getChannelDescription())
                .position((int) channelCount)
                .isDefault(false)
                .lastActive(Instant.now())
                .build();
        channel = conversationRepository.save(channel);

        log.info("Channel '{}' created in server '{}' by user '{}'", 
                channel.getChannelName(), serverId, user.getName());

        return CreateChannelResponse.builder()
                .channel(channel)
                .build();
    }

    @Override
    public List<ChannelDto> getChannels(Principal principal, String serverId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Verify user is a member
        if (!serverMembershipRepository.existsByServerIdAndUserId(serverId, user.getId())) {
            throw new ServerMembershipNotFoundException();
        }

        List<Conversation> channels = conversationRepository.findByServerIdOrderByPositionAsc(serverId);

        return channels.stream().map(this::toChannelDto).collect(Collectors.toList());
    }

    @Override
    public ChannelDto getChannelDetails(Principal principal, String serverId, String channelId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Verify user is a member
        if (!serverMembershipRepository.existsByServerIdAndUserId(serverId, user.getId())) {
            throw new ServerMembershipNotFoundException();
        }

        Conversation channel = conversationRepository.findById(channelId)
                .orElseThrow(ConversationNotFoundException::new);

        // Verify channel belongs to server
        if (!serverId.equals(channel.getServerId())) {
            throw new ConversationNotFoundException();
        }

        return toChannelDto(channel);
    }

    @Override
    @Transactional
    public Conversation updateChannel(Principal principal, String serverId, String channelId, 
                                       UpdateChannelRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Check permission
        ServerMembership membership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, user.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        if (membership.getRole() == ServerRole.MEMBER) {
            throw new InsufficientPermissionException("Only admin or owner can update channels");
        }

        Conversation channel = conversationRepository.findById(channelId)
                .orElseThrow(ConversationNotFoundException::new);

        // Verify channel belongs to server
        if (!serverId.equals(channel.getServerId())) {
            throw new ConversationNotFoundException();
        }

        if (request.getChannelName() != null) {
            channel.setChannelName(request.getChannelName());
        }
        if (request.getChannelDescription() != null) {
            channel.setChannelDescription(request.getChannelDescription());
        }
        if (request.getPosition() != null) {
            channel.setPosition(request.getPosition());
        }

        return conversationRepository.save(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(Principal principal, String serverId, String channelId) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        // Check permission
        ServerMembership membership = serverMembershipRepository
                .findByServerIdAndUserId(serverId, user.getId())
                .orElseThrow(ServerMembershipNotFoundException::new);

        if (membership.getRole() == ServerRole.MEMBER) {
            throw new InsufficientPermissionException("Only admin or owner can delete channels");
        }

        Conversation channel = conversationRepository.findById(channelId)
                .orElseThrow(ConversationNotFoundException::new);

        // Verify channel belongs to server
        if (!serverId.equals(channel.getServerId())) {
            throw new ConversationNotFoundException();
        }

        // Cannot delete default channel if it's the only one
        if (Boolean.TRUE.equals(channel.getIsDefault())) {
            // Check if there are other channels
            Conversation otherChannel = conversationRepository
                    .findFirstByServerIdAndIdNot(serverId, channelId)
                    .orElse(null);

            if (otherChannel == null) {
                throw new CannotDeleteDefaultChannelException();
            }

            // Transfer default to another channel
            otherChannel.setIsDefault(true);
            conversationRepository.save(otherChannel);
        }

        conversationRepository.delete(channel);
        log.info("Channel '{}' deleted from server '{}' by user '{}'",
                channel.getChannelName(), serverId, user.getName());
    }

    private ChannelDto toChannelDto(Conversation channel) {
        return ChannelDto.builder()
                .id(channel.getId())
                .serverId(channel.getServerId())
                .channelName(channel.getChannelName())
                .channelDescription(channel.getChannelDescription())
                .position(channel.getPosition())
                .isDefault(channel.getIsDefault())
                .lastMessageID(channel.getLastMessageID())
                .lastMessageContent(channel.getLastMessageContent())
                .lastActive(channel.getLastActive())
                .build();
    }
}

