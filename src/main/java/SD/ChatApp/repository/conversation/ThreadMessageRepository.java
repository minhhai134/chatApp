package SD.ChatApp.repository.conversation;

import SD.ChatApp.model.conversation.ThreadMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadMessageRepository extends JpaRepository<ThreadMessage, Long> {
    
    @Query("SELECT tm FROM ThreadMessage tm WHERE tm.threadId = :threadId AND tm.id < :pivotId ORDER BY tm.id DESC")
    List<ThreadMessage> findByThreadIdWithPivot(@Param("threadId") String threadId, @Param("pivotId") Long pivotId);
    
    @Query("SELECT tm FROM ThreadMessage tm WHERE tm.threadId = :threadId ORDER BY tm.id DESC")
    List<ThreadMessage> findByThreadIdOrderByIdDesc(@Param("threadId") String threadId);
}
