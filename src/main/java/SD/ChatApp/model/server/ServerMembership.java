package SD.ChatApp.model.server;

import SD.ChatApp.enums.ServerRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "server_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"server_id", "user_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "server_id", nullable = false)
    private String serverId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ServerRole role = ServerRole.MEMBER;

    @Column(length = 50)
    private String nickname;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;
}

