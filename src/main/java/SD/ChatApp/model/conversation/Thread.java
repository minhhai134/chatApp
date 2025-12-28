package SD.ChatApp.model.conversation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "threads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Thread {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private String threadName;

    private Long lastMessageId;

    private String lastMessageContent;

    private Instant lastActive;

    @Column(nullable = false)
    private String creatorId;

    @Column(nullable = false)
    private Instant createdAt;
}
