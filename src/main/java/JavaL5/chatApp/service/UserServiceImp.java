package JavaL5.chatApp.service;


import JavaL5.chatApp.dto.user.CreateUserRequest;
import JavaL5.chatApp.exception.UserExistedException;
import JavaL5.chatApp.model.App;
import JavaL5.chatApp.model.User;
import JavaL5.chatApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Override
    public User createUser(App app, CreateUserRequest request) {

        if(userRepository.findByAppIdAndClientUserId(app.getId(), request.getClientUserId()).isPresent())
        { throw new UserExistedException(); }

        log.info("User not exist, create now {}", app);

        User user = User.builder().
                         appId(app.getId()).
                         clientUserId(request.getClientUserId()).
                         userName(request.getUserName()).
                         build();

        return userRepository.save(user);

    }
}
