package JavaL5.chatApp.repository;

import JavaL5.chatApp.model.ChannelMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelMembersRepository extends JpaRepository<ChannelMembers,String> {
}
