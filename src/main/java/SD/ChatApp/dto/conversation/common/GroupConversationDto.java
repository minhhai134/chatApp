package SD.ChatApp.dto.conversation.common;

import SD.ChatApp.enums.Conversation_Type;
import SD.ChatApp.enums.Membership_Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class GroupConversationDto {
    private String conversationId;

    private Conversation_Type conversationType;

    private Instant conversationLastActive;

    private long lastMessageID;

    private String lastMessageContent;

    private Instant lastSeen;

    private String membershipId;

    private String adminId;

    private String groupName;

    private Membership_Status membershipStatus;

    // Constructor for queries without type (Server-Centric model)
    public GroupConversationDto(String conversationId, Instant conversationLastActive, long lastMessageID,
            String lastMessageContent, Instant lastSeen, String membershipId, String adminId,
            String groupName, Membership_Status membershipStatus) {
        this.conversationId = conversationId;
        this.conversationType = Conversation_Type.Group; // Default to Group
        this.conversationLastActive = conversationLastActive;
        this.lastMessageID = lastMessageID;
        this.lastMessageContent = lastMessageContent;
        this.lastSeen = lastSeen;
        this.membershipId = membershipId;
        this.adminId = adminId;
        this.groupName = groupName;
        this.membershipStatus = membershipStatus;
    }

}
