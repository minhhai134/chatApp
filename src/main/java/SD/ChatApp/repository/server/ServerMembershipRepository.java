package SD.ChatApp.repository.server;

import SD.ChatApp.model.server.ServerMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerMembershipRepository extends JpaRepository<ServerMembership, String> {

    Optional<ServerMembership> findByServerIdAndUserId(String serverId, String userId);

    List<ServerMembership> findByServerId(String serverId);

    List<ServerMembership> findByUserId(String userId);

    long countByServerId(String serverId);

    boolean existsByServerIdAndUserId(String serverId, String userId);

    @Transactional
    void deleteByServerIdAndUserId(String serverId, String userId);

    @Transactional
    void deleteByServerId(String serverId);
}

