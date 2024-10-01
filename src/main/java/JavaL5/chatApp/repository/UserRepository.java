package JavaL5.chatApp.repository;

import JavaL5.chatApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    Optional<User> findByAppIdAndClientUserId(String appId, String clientUserId);
}
