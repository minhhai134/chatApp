package SD.ChatApp.service.conversation;

import SD.ChatApp.dto.thread.CreateThreadRequest;
import SD.ChatApp.dto.websocket.thread.ThreadMessageReceiving;
import SD.ChatApp.dto.websocket.thread.ThreadMessageSending;
import SD.ChatApp.enums.Message_Status;
import SD.ChatApp.enums.Message_Type;
import SD.ChatApp.exception.conversation.ConversationNotFoundException;
import SD.ChatApp.exception.conversation.MessageNotFoundException;
import SD.ChatApp.exception.conversation.ThreadAlreadyExistsException;
import SD.ChatApp.exception.conversation.ThreadNotFoundException;
import SD.ChatApp.exception.user.UserNotFoundException;
import SD.ChatApp.model.User;
import SD.ChatApp.model.conversation.Message;
import SD.ChatApp.model.conversation.Thread;
import SD.ChatApp.model.conversation.ThreadMessage;
import SD.ChatApp.repository.UserRepository;
import SD.ChatApp.repository.conversation.MessageRepository;
import SD.ChatApp.repository.conversation.ThreadMessageRepository;
import SD.ChatApp.repository.conversation.ThreadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadServiceImpl implements ThreadService {

    private final ThreadRepository threadRepository;
    private final ThreadMessageRepository threadMessageRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private static final int MESSAGE_PAGE_SIZE = 20;

    @Override
    @Transactional
    public Thread createThread(Principal principal, CreateThreadRequest request) {
        log.info("🧵 Creating thread for message: {}", request.getMessageId());

        // Validate message ID
        if (request.getMessageId() == null) {
            log.error("❌ Message ID is null");
            throw new IllegalArgumentException("Message ID cannot be null");
        }

        // Get the user
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> {
                    log.error("❌ User not found: {}", principal.getName());
                    return new UserNotFoundException();
                });
        log.info("✅ Found user: {} (ID: {})", user.getName(), user.getId());

        // Get the parent message
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> {
                    log.error("❌ Message not found with ID: {}", request.getMessageId());
                    return new MessageNotFoundException();
                });
        log.info("✅ Found message: ID={}, ConversationID={}", message.getId(), message.getConversationId());

        // Check if thread already exists for this message
        if (threadRepository.existsByMessageId(request.getMessageId())) {
            log.error("❌ Thread already exists for message ID: {}", request.getMessageId());
            throw new ThreadAlreadyExistsException();
        }

        // Create thread
        Thread thread = Thread.builder()
                .conversationId(message.getConversationId())
                .messageId(message.getId())
                .threadName(request.getThreadName())
                .creatorId(user.getId())
                .createdAt(Instant.now())
                .lastActive(Instant.now())
                .build();

        Thread savedThread = threadRepository.save(thread);
        log.info("✅ Thread saved: ID={}", savedThread.getId());

        // Update parent message to mark it has a thread
        message.setHaveThread(true);
        messageRepository.save(message);
        log.info("✅ Parent message updated with haveThread=true");

        log.info("🎉 Thread created successfully with ID: {}", savedThread.getId());
        return savedThread;
    }

    @Override
    public Thread getThread(String threadId) {
        return threadRepository.findById(threadId)
                .orElseThrow(ThreadNotFoundException::new);
    }

    @Override
    public List<Thread> getThreadsByConversation(String conversationId) {
        return threadRepository.findByConversationIdOrderByLastActiveDesc(conversationId);
    }

    @Override
    @Transactional
    public ThreadMessageReceiving sendThreadMessage(Principal principal, ThreadMessageSending message) {
        log.info("📥 Processing thread message from {}: {}", principal.getName(), message);

        // Get user
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        log.info("✅ Found sender: {} (ID: {})", sender.getName(), sender.getId());

        // Get thread
        Thread thread = threadRepository.findById(message.getThreadId())
                .orElseThrow(ThreadNotFoundException::new);
        log.info("✅ Found thread: {} (ID: {})", thread.getThreadName(), thread.getId());

        // Save message
        ThreadMessage threadMessage = ThreadMessage.builder()
                .threadId(message.getThreadId())
                .conversationId(thread.getConversationId())
                .senderId(sender.getId())
                .sentTime(Instant.now())
                .type(Message_Type.TEXT_MESSAGE)
                .content(message.getContent())
                .messageStatus(Message_Status.UNSEEN)
                .build();

        ThreadMessage savedMessage = threadMessageRepository.save(threadMessage);
        log.info("✅ Saved thread message to database with ID: {}", savedMessage.getId());

        // Update thread's lastActive and lastMessageId
        thread.setLastActive(savedMessage.getSentTime());
        thread.setLastMessageId(savedMessage.getId());
        thread.setLastMessageContent(savedMessage.getContent());
        threadRepository.save(thread);

        // Build response
        ThreadMessageReceiving response = ThreadMessageReceiving.builder()
                .id(savedMessage.getId())
                .threadId(savedMessage.getThreadId())
                .conversationId(savedMessage.getConversationId())
                .senderId(savedMessage.getSenderId())
                .senderName(sender.getName())
//                .senderAvatar(sender.getUserAvt())
                .sentTime(savedMessage.getSentTime())
                .type(savedMessage.getType())
                .content(savedMessage.getContent())
                .messageStatus(savedMessage.getMessageStatus())
                .build();
        
        log.info("✅ Built ThreadMessageReceiving response: {}", response);
        return response;
    }

    @Override
    public List<ThreadMessage> getThreadMessages(String threadId, Long pivotId) {
        log.info("Getting thread messages for threadId: {}, pivotId: {}", threadId, pivotId);

        // Verify thread exists
        threadRepository.findById(threadId)
                .orElseThrow(ThreadNotFoundException::new);

        List<ThreadMessage> messages;
        
        if (pivotId == null || pivotId == 0) {
            // First load - get latest messages
            messages = threadMessageRepository.findByThreadIdOrderByIdDesc(threadId);
            if (messages.size() > MESSAGE_PAGE_SIZE) {
                messages = messages.subList(0, MESSAGE_PAGE_SIZE);
            }
        } else {
            // Load older messages before pivot
            messages = threadMessageRepository.findByThreadIdWithPivot(threadId, pivotId);
            if (messages.size() > MESSAGE_PAGE_SIZE) {
                messages = messages.subList(0, MESSAGE_PAGE_SIZE);
            }
        }

        log.info("Found {} thread messages", messages.size());
        return messages;
    }
}
