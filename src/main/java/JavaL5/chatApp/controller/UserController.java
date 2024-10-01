package JavaL5.chatApp.controller;


import JavaL5.chatApp.dto.user.CreateUserRequest;
import JavaL5.chatApp.dto.user.CreateUserResponse;
import JavaL5.chatApp.model.User;
import JavaL5.chatApp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        User user = userService.createUser(getAuthenticatedApp(),request);
        return ResponseEntity.status(HttpStatus.OK).body(CreateUserResponse.builder().user(user).build());

    }
}
