package SD.ChatApp.model.conversation;

import SD.ChatApp.enums.Message_Status;
import SD.ChatApp.enums.Message_Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "thread_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String threadId;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private Instant sentTime;

    @Enumerated(EnumType.STRING)
    private Message_Type type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Message_Status messageStatus;
}
