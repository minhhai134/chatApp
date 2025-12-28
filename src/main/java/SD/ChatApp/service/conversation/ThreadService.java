package SD.ChatApp.service.conversation;

import SD.ChatApp.dto.thread.CreateThreadRequest;
import SD.ChatApp.dto.websocket.thread.ThreadMessageReceiving;
import SD.ChatApp.dto.websocket.thread.ThreadMessageSending;
import SD.ChatApp.model.conversation.Thread;
import SD.ChatApp.model.conversation.ThreadMessage;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public interface ThreadService {
    
    Thread createThread(Principal principal, CreateThreadRequest request);
    
    Thread getThread(String threadId);
    
    List<Thread> getThreadsByConversation(String conversationId);
    
    ThreadMessageReceiving sendThreadMessage(Principal principal, ThreadMessageSending message);
    
    List<ThreadMessage> getThreadMessages(String threadId, Long pivotId);
}
