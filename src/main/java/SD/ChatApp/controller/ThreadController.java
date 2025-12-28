package SD.ChatApp.controller;

import SD.ChatApp.dto.thread.CreateThreadRequest;
import SD.ChatApp.dto.thread.CreateThreadResponse;
import SD.ChatApp.dto.thread.GetThreadMessagesResponse;
import SD.ChatApp.dto.websocket.thread.ThreadMessageReceiving;
import SD.ChatApp.dto.websocket.thread.ThreadMessageSending;
import SD.ChatApp.model.conversation.Thread;
import SD.ChatApp.model.conversation.ThreadMessage;
import SD.ChatApp.service.conversation.ThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ThreadController {

    private final ThreadService threadService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Create a new thread from a message
     */
    @PostMapping("/create")
    public ResponseEntity<CreateThreadResponse> createThread(
            Principal principal,
            @RequestBody CreateThreadRequest request) {
        
        log.info("🧵 Creating thread: {}", request);
        Thread thread = threadService.createThread(principal, request);
        log.info("✅ Thread created with ID: {}, ConversationID: {}", thread.getId(), thread.getConversationId());
        
        // Notify all conversation participants about the new thread via conversation topic
        String destination = "/topic/" + thread.getConversationId();
        Map<String, Object> notification = Map.of(
            "type", "THREAD_CREATED",
            "thread", thread
        );
        
        log.info("📤 Broadcasting THREAD_CREATED notification to: {}", destination);
        log.info("📦 Notification payload: {}", notification);
        messagingTemplate.convertAndSend(destination, notification);
        log.info("✅ Thread creation notification sent successfully");
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CreateThreadResponse.builder().thread(thread).build());
    }

    /**
     * Get thread details
     */
    @GetMapping("/{threadId}")
    public ResponseEntity<Thread> getThread(@PathVariable String threadId) {
        Thread thread = threadService.getThread(threadId);
        return ResponseEntity.ok(thread);
    }

    /**
     * Get all threads in a conversation
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Thread>> getThreadsByConversation(
            @PathVariable String conversationId) {
        
        List<Thread> threads = threadService.getThreadsByConversation(conversationId);
        return ResponseEntity.ok(threads);
    }

    /**
     * Get thread messages with pivot pagination
     */
    @GetMapping("/{threadId}/messages")
    public ResponseEntity<GetThreadMessagesResponse> getThreadMessages(
            @PathVariable String threadId,
            @RequestParam(required = false, defaultValue = "0") Long pivotId) {
        
        log.info("Getting messages for thread: {}, pivot: {}", threadId, pivotId);
        List<ThreadMessage> messages = threadService.getThreadMessages(threadId, pivotId);
        
        return ResponseEntity.ok(
                GetThreadMessagesResponse.builder().messages(messages).build()
        );
    }

    /**
     * WebSocket handler for sending messages in a thread
     */
    @MessageMapping("/thread_message")
    public ThreadMessageReceiving sendThreadMessage(
            Principal principal,
            ThreadMessageSending message) {
        
        log.info("📥 Received thread message from {}: {}", principal.getName(), message);
        
        ThreadMessageReceiving threadMessage = threadService.sendThreadMessage(principal, message);
        
        if (threadMessage == null) {
            log.error("❌ Thread message service returned null");
            return null;
        }

        // Send to thread topic so all participants can receive
        String destination = "/topic/thread-" + message.getThreadId();
        log.info("📤 Broadcasting thread message to: {}", destination);
        messagingTemplate.convertAndSend(destination, threadMessage);
        log.info("✅ Thread message broadcast complete: {}", threadMessage.getId());

        return threadMessage;
    }
}
