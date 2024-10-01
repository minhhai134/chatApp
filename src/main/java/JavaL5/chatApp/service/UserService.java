package JavaL5.chatApp.service;

import JavaL5.chatApp.dto.user.CreateUserRequest;
import JavaL5.chatApp.model.App;
import JavaL5.chatApp.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
     User createUser(App app, CreateUserRequest createUserRequest);


}
