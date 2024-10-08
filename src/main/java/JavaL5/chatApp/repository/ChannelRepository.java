package JavaL5.chatApp.repository;

import JavaL5.chatApp.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,String> {
    Optional<Channel> findByAppIdAndClientChannelId(String appId, String clientChannelId);
}
