package JavaL5.chatApp.service;

import JavaL5.chatApp.dto.message.SendMessageRequest;
import JavaL5.chatApp.model.App;
import JavaL5.chatApp.model.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    Message sendMessage(App app, String channelId, SendMessageRequest request);

    List<Message> getMessages(String channelID, String pivotId, int pivot);

}
