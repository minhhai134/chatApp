package JavaL5.chatApp.repository;

import JavaL5.chatApp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageRepository extends JpaRepository<Message,String> {

    @Query(value = "Select * from Message where id < :id and channel_id = :channelId Limit :pivot", nativeQuery = true)
    List<Message> getMessagesByPivot(@Param(value = "channelId") String channelId,
                                     @Param(value = "id")String pivotId,
                                     @Param(value = "pivot")int pivot);
}
