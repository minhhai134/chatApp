package JavaL5.chatApp;

import JavaL5.chatApp.controller.ChannelController;
import JavaL5.chatApp.model.App;
import JavaL5.chatApp.repository.AppRepository;
import JavaL5.chatApp.service.ChannelService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ChatAppApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ChatAppApplication.class, args);


		AppRepository appRepository = context.getBean(AppRepository.class);
		ChannelController channelController = context.getBean(ChannelController.class);

		ChannelService service = context.getBean(ChannelService.class);

		App app = appRepository.save(App.builder().appName("App1").appApiKey("engineer-pro-key").build());

	}




}
