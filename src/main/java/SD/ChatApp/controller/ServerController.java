package SD.ChatApp.controller;

import SD.ChatApp.dto.server.*;
import SD.ChatApp.model.server.Server;
import SD.ChatApp.service.server.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/server")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @PostMapping
    public ResponseEntity<CreateServerResponse> createServer(
            Principal principal,
            @Valid @RequestBody CreateServerRequest request) {
        CreateServerResponse response = serverService.createServer(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<GetServerListResponse> getUserServers(Principal principal) {
        List<ServerDto> servers = serverService.getUserServers(principal);
        return ResponseEntity.ok(GetServerListResponse.builder().servers(servers).build());
    }

    @GetMapping("/{serverId}")
    public ResponseEntity<ServerDto> getServerDetails(
            Principal principal,
            @PathVariable String serverId) {
        ServerDto server = serverService.getServerDetails(principal, serverId);
        return ResponseEntity.ok(server);
    }

    @PatchMapping("/{serverId}")
    public ResponseEntity<Server> updateServer(
            Principal principal,
            @PathVariable String serverId,
            @Valid @RequestBody UpdateServerRequest request) {
        Server server = serverService.updateServer(principal, serverId, request);
        return ResponseEntity.ok(server);
    }

    @DeleteMapping("/{serverId}")
    public ResponseEntity<Void> deleteServer(
            Principal principal,
            @PathVariable String serverId) {
        serverService.deleteServer(principal, serverId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{serverId}/join")
    public ResponseEntity<JoinServerResponse> joinServer(
            Principal principal,
            @PathVariable String serverId) {
        JoinServerResponse response = serverService.joinServer(principal, serverId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{serverId}/leave")
    public ResponseEntity<Void> leaveServer(
            Principal principal,
            @PathVariable String serverId) {
        serverService.leaveServer(principal, serverId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{serverId}/members")
    public ResponseEntity<GetServerMembersResponse> getServerMembers(
            Principal principal,
            @PathVariable String serverId) {
        List<ServerMemberDto> members = serverService.getServerMembers(principal, serverId);
        return ResponseEntity.ok(GetServerMembersResponse.builder().members(members).build());
    }

    @DeleteMapping("/{serverId}/members/{userId}")
    public ResponseEntity<Void> kickMember(
            Principal principal,
            @PathVariable String serverId,
            @PathVariable String userId) {
        serverService.kickMember(principal, serverId, userId);
        return ResponseEntity.noContent().build();
    }
}

