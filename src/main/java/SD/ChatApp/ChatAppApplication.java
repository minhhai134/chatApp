package SD.ChatApp;

import SD.ChatApp.model.User;
import SD.ChatApp.enums.Role;
import SD.ChatApp.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ChatAppApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(ChatAppApplication.class, args);

		UserRepository userRepository = context.getBean(UserRepository.class);

		// Only create users if database is empty
		if (userRepository.count() == 0) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			for (int i=1;i<=4;i++) {
				userRepository.save(User.builder().
						username("username"+Integer.toString(i)).
						password(passwordEncoder.encode("1")).
						name("user"+Integer.toString(i)).
						onlineStatus("on").
						role(Role.USER).
						build());
			}
			System.out.println("Created 4 test users: username1-username4 with password '1'");
		}
	}

}
