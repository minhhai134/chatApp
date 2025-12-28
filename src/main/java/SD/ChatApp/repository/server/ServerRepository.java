package SD.ChatApp.repository.server;

import SD.ChatApp.model.server.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<Server, String> {

    @Query("SELECT s FROM Server s " +
           "JOIN ServerMembership sm ON s.id = sm.serverId " +
           "WHERE sm.userId = :userId " +
           "ORDER BY s.updatedAt DESC")
    List<Server> findServersByUserId(@Param("userId") String userId);

    List<Server> findByOwnerId(String ownerId);
}

