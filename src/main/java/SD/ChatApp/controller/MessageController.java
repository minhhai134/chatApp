package SD.ChatApp.controller;

import SD.ChatApp.dto.websocket.message.ChatMessageReceiving;
import SD.ChatApp.dto.websocket.message.ChatMessageSending;
import SD.ChatApp.dto.message.GetMessagesResponse;
import SD.ChatApp.exception.user.UserNotFoundException;
import SD.ChatApp.model.User;
import SD.ChatApp.model.conversation.Message;
import SD.ChatApp.enums.Conversation_Type;
import SD.ChatApp.enums.Membership_Status;
import SD.ChatApp.enums.Message_Type;
import SD.ChatApp.repository.UserRepository;
import SD.ChatApp.service.conversation.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/one_to_one_chat")
    public ChatMessageReceiving sendOneToOneMessage(Principal principal, ChatMessageSending input) throws JsonProcessingException {
        log.info("💬 Got one-to-one message input: {}", input);

        ChatMessageReceiving chatMessage = messageService.sendMessage(principal, input);
        if(chatMessage==null) return null;

        // Broadcast to conversation topic - both sender and receiver are subscribed
        // This is the single source of truth for message delivery
        String conversationTopic = "/topic/" + input.getConversationId();
        messagingTemplate.convertAndSend(conversationTopic, chatMessage);
        log.info("📢 Broadcast message to conversation topic: {}", conversationTopic);

        return chatMessage;
    }

    @MessageMapping("/group_chat")
    public ChatMessageReceiving sendGroupMessage(Principal principal, ChatMessageSending input) throws JsonProcessingException{
        log.info("📥 GROUP_CHAT received from user: {}", principal.getName());
        log.info("📦 Message payload: {}", input);
        log.info("🔍 ConversationId: {}, DestinationId: {}, Type: {}", 
                input.getConversationId(), input.getDestinationId(), input.getConversationType());
        
        ChatMessageReceiving chatMessage = messageService.sendMessage(principal, input);
        
        if(chatMessage==null) {
            log.error("❌ Message service returned null!");
            return null;
        }
        
        log.info("✅ Message saved with ID: {}", chatMessage.getMessage().getId());
        log.info("📤 Broadcasting to topic: /topic/{}", input.getDestinationId());
        
        messagingTemplate.convertAndSend("/topic/"+input.getDestinationId(), chatMessage);
        
        log.info("✅ Message broadcast complete");
        return chatMessage;
    }

//    @GetMapping("{conversationId}")
    @GetMapping
    public ResponseEntity<GetMessagesResponse> getMessages(
            Principal principal,
            @RequestParam String conversationId,
            @RequestParam long pivotId){

        List<Message> list = messageService.getMessages(principal, conversationId, pivotId);
        return ResponseEntity.status(HttpStatus.OK).body(GetMessagesResponse.builder().messages(list).build());
    }


    @PostMapping("/files")
    public ResponseEntity<ChatMessageReceiving> sendFile(
            Principal principal,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("conversationId") String conversationId,
            @RequestHeader("destinationId") String destinationId,
            @RequestHeader("sentTime") Instant sentTime,
            @RequestHeader("membershipStatus") Membership_Status membershipStatus,
            @RequestHeader("conversationType") Conversation_Type conversationType
            ){

        ChatMessageSending message = ChatMessageSending.builder()
                .conversationId(conversationId)
                .destinationId(destinationId)
                .sentTime(sentTime)
                .membershipStatus(membershipStatus)
                .messageType(Message_Type.FILE_MESSAGE)
                .conversationType(conversationType)
                .build();

        ChatMessageReceiving chatMessage = messageService.sendFile(principal, message, file);
        if(chatMessage==null) return null;

        // Broadcast to conversation topic - single source of truth for file messages
        String topic = "/topic/" + conversationId;
        messagingTemplate.convertAndSend(topic, chatMessage);
        log.info("📢 Broadcast file message to topic: {}", topic);

        return ResponseEntity.status(HttpStatus.OK).body(chatMessage);
    }

}
