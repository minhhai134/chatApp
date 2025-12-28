package SD.ChatApp.repository.conversation;

import SD.ChatApp.model.conversation.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value ="select m from Message m where m.conversationId = :cv_id " +
            "and m.id < :pivot order by id desc LIMIT 10" )
    List<Message> getMessage(@Param("cv_id") String conversationId, @Param("pivot") long pivotId);



}
