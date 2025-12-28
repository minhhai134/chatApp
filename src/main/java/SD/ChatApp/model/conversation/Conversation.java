package SD.ChatApp.model.conversation;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Server reference - required for server channels, nullable for legacy DM conversations
    // TODO: Make NOT NULL after full migration to Server-Centric model
    @Column(name = "server_id")
    private String serverId;

    // Channel metadata
    @Column(name = "channel_name", length = 100)
    private String channelName;

    @Column(name = "channel_description", columnDefinition = "TEXT")
    private String channelDescription;

    @Column(name = "position")
    @Builder.Default
    private Integer position = 0;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    // Existing fields preserved for message tracking
    private long lastMessageID;

    private String lastMessageContent;

    private Instant lastActive;

}
