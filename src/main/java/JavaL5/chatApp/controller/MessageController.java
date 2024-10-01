package JavaL5.chatApp.controller;

import JavaL5.chatApp.dto.message.GetMessagesResponse;
import JavaL5.chatApp.dto.message.SendMessageRequest;
import JavaL5.chatApp.dto.message.SendMessageResponse;
import JavaL5.chatApp.model.Message;
import JavaL5.chatApp.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/messages")
public class MessageController extends BaseController {

    private static final int PIVOT = 5;

    @Autowired
    private MessageService messageService;

    @PostMapping("/{channelId}")
    public ResponseEntity<SendMessageResponse> sendMessage(@PathVariable String channelId,
            @Valid @RequestBody SendMessageRequest request){

        Message message = messageService.sendMessage(getAuthenticatedApp(),channelId,request);

        return ResponseEntity.ok(SendMessageResponse.builder().message(message).build());
    }

    @GetMapping("/{channelId}/{pivotId}")
    public ResponseEntity<GetMessagesResponse> getMessages(@PathVariable String channelId,@PathVariable String pivotId){

        List<Message> messageList = messageService.getMessages(channelId,pivotId,PIVOT);

        return ResponseEntity.ok(GetMessagesResponse.builder().messageList(messageList).build());
    }
}
