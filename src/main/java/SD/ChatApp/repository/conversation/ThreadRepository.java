package SD.ChatApp.repository.conversation;

import SD.ChatApp.model.conversation.Thread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, String> {
    
    Optional<Thread> findByMessageId(Long messageId);
    
    List<Thread> findByConversationIdOrderByLastActiveDesc(String conversationId);
    
    boolean existsByMessageId(Long messageId);
}
