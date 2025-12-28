package SD.ChatApp.service.channel;

import SD.ChatApp.dto.channel.*;
import SD.ChatApp.model.conversation.Conversation;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public interface ChannelService {

    CreateChannelResponse createChannel(Principal principal, String serverId, CreateChannelRequest request);

    List<ChannelDto> getChannels(Principal principal, String serverId);

    ChannelDto getChannelDetails(Principal principal, String serverId, String channelId);

    Conversation updateChannel(Principal principal, String serverId, String channelId, UpdateChannelRequest request);

    void deleteChannel(Principal principal, String serverId, String channelId);
}

