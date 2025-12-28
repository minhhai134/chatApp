package SD.ChatApp.controller;

import SD.ChatApp.dto.channel.*;
import SD.ChatApp.model.conversation.Conversation;
import SD.ChatApp.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/server/{serverId}/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<CreateChannelResponse> createChannel(
            Principal principal,
            @PathVariable String serverId,
            @Valid @RequestBody CreateChannelRequest request) {
        CreateChannelResponse response = channelService.createChannel(principal, serverId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<GetChannelsResponse> getChannels(
            Principal principal,
            @PathVariable String serverId) {
        List<ChannelDto> channels = channelService.getChannels(principal, serverId);
        return ResponseEntity.ok(GetChannelsResponse.builder().channels(channels).build());
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDto> getChannelDetails(
            Principal principal,
            @PathVariable String serverId,
            @PathVariable String channelId) {
        ChannelDto channel = channelService.getChannelDetails(principal, serverId, channelId);
        return ResponseEntity.ok(channel);
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<Conversation> updateChannel(
            Principal principal,
            @PathVariable String serverId,
            @PathVariable String channelId,
            @Valid @RequestBody UpdateChannelRequest request) {
        Conversation channel = channelService.updateChannel(principal, serverId, channelId, request);
        return ResponseEntity.ok(channel);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            Principal principal,
            @PathVariable String serverId,
            @PathVariable String channelId) {
        channelService.deleteChannel(principal, serverId, channelId);
        return ResponseEntity.noContent().build();
    }
}

