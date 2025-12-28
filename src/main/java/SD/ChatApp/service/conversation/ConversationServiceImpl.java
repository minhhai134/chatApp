package SD.ChatApp.service.conversation;

import SD.ChatApp.dto.conversation.common.ChangeMembershipStatusRequest;
import SD.ChatApp.dto.conversation.common.GetConversationListResponse;
import SD.ChatApp.dto.conversation.common.GroupConversationDto;
import SD.ChatApp.dto.conversation.group.*;
import SD.ChatApp.dto.conversation.onetoone.CreateOneToOneConversationRequest;
import SD.ChatApp.dto.conversation.onetoone.CreateOneToOneConversationResponse;
import SD.ChatApp.dto.conversation.common.OneToOneConversationDto;
import SD.ChatApp.dto.websocket.conversation.NewGroupNotification;
import SD.ChatApp.dto.websocket.conversation.NewOneToOneChatNotification;
import SD.ChatApp.dto.websocket.conversation.RemovedNotification;
import SD.ChatApp.exception.conversation.LeaveGroupException;
import SD.ChatApp.exception.user.UserNotFoundException;
import SD.ChatApp.model.User;
import SD.ChatApp.model.conversation.Conversation;
import SD.ChatApp.model.conversation.GroupMetaData;
import SD.ChatApp.model.conversation.Membership;
import SD.ChatApp.enums.Membership_Status;
import SD.ChatApp.enums.Notification_Type;
import SD.ChatApp.repository.UserRepository;
import SD.ChatApp.repository.conversation.ConversationRepository;
import SD.ChatApp.repository.conversation.GroupMetaDataRepository;
import SD.ChatApp.repository.conversation.MembershipRepository;
import SD.ChatApp.service.network.BlockService;
import SD.ChatApp.service.network.FriendServiceImpl;
import SD.ChatApp.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final BlockService blockService;
    private final MembershipRepository membershipRepository;
    private final GroupMetaDataRepository groupMetaDataRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendServiceImpl friendService;

    public CreateOneToOneConversationResponse createOneToOneConversation(
            Principal principal,
            CreateOneToOneConversationRequest request){
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        User friend = userRepository.findById(request.getFriendId()).
                orElseThrow(UserNotFoundException::new);

        if(blockService.checkBlockstatus(user.getId(), friend.getId())
                || blockService.checkBlockstatus(friend.getId(), user.getId())) {
            throw new UserNotFoundException();
        }

            if (!conversationRepository.checkConversationExisted(user.getId(), friend.getId()).isEmpty()) {
                log.info("{}", conversationRepository.checkConversationExisted(user.getId(), friend.getId()));
                throw new UserNotFoundException();
            }
//        } catch (Exception e) {
//            log.info("Error: {}", e.toString());
//        }


        // In Server-Centric model, this creates a DM channel within a DM server context
        // For now, we create a conversation without serverId for backward compatibility
        // TODO: Migrate to proper DM server structure
        Conversation newConversation = conversationRepository.save(
                Conversation.builder().
                        channelName("DM").
                        lastActive(request.getLastActive()).
                        build()
        );

        Membership myMembership = membershipRepository.save(
                Membership.builder().
                        conversationId(newConversation.getId()).
                        userId(user.getId()).
                        status(Membership_Status.ACTIVE).
                        lastSeen(request.getLastActive()).
                        build()
        );

        Membership friendMembership = Membership.builder().
                        conversationId(newConversation.getId()).
                        userId(friend.getId()).
                        status(Membership_Status.PENDING).
                        build();

        if(friendService.checkFriendRelationship(user.getId(), friend.getId())){
            friendMembership.setStatus(Membership_Status.ACTIVE);
        }

        membershipRepository.save(friendMembership);
        log.info("Friend Membership: {}", friendMembership);
        /*
        Send notification
         */
        OneToOneConversationDto newConv = conversationRepository.getOnetoOneConversationById(
                newConversation.getId(), friend.getId()).getFirst();
        newConv.setMembershipStatus(friendMembership.getStatus());
        NewOneToOneChatNotification notification = NewOneToOneChatNotification.builder().
                newConversation(newConv).notificationType(Notification_Type.NEW_CONVERSATION)
                .build();
        messagingTemplate.convertAndSendToUser(
                friend.getUsername(), "/queue/messages", notification);


        return CreateOneToOneConversationResponse.builder()
                .conversation(newConversation)
                .membership(myMembership)
                .build();
    }

    public GetConversationListResponse getConversationList(
            Principal principal,
            Membership_Status membershipStatus){
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        log.info("Membership: {}", membershipStatus);

//        String memberShip_status = membershipStatus.name();
        List<OneToOneConversationDto> oneToOneList = conversationRepository.getOnetoOneConversationList(user.getId(), membershipStatus);
        oneToOneList.forEach(response -> {
            if(blockService.checkBlockstatus(user.getId(), response.getFriendId()))
                response.setBlocker(user.getId());
            else if(blockService.checkBlockstatus(response.getFriendId() , user.getId())){
                response.setBlocker(response.getFriendId());
                    }
        }
                );
        List<GroupConversationDto> groupList = conversationRepository.getGroupConversationList(user.getId(), membershipStatus);

        return GetConversationListResponse.builder().
                oneToOneList(oneToOneList).
                groupList(groupList).
                build();
    }

    public Membership changeMembershipStatus(
            Principal principal,
            ChangeMembershipStatusRequest request){
//        log.info("Request: {}", request);
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Membership membership = membershipRepository.findByConversationIdAndUserId(request.getConversationId(), user.getId() ).orElseThrow();
        membership.setStatus(request.getNewStatus());
//        log.info("Membership: {}", membership);
        return membershipRepository.save(membership);

    }

    public CreateGroupResponse createGroupConversation(
            Principal principal,
            CreateGroupRequest request){
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        // In Server-Centric model, groups should be created as servers with channels
        // For backward compatibility, we create a conversation without serverId
        // TODO: Migrate to proper server structure
        Conversation newConversation = conversationRepository.save(
                Conversation.builder().
                        channelName(request.getGroupName()).
                        lastActive(Instant.now()).
                        build()
        );

        Membership adminMembership = membershipRepository.save(
                Membership.builder().
                        conversationId(newConversation.getId()).
                        userId(user.getId()).
                        status(Membership_Status.ACTIVE).
                        build()
        );

        GroupMetaData metaData = groupMetaDataRepository.save(
                GroupMetaData.builder().
                        groupId(newConversation.getId()).
                        adminId(user.getId()).
                        groupName(request.getGroupName()).
                        build()
        );

        return CreateGroupResponse.builder().
                conversation(newConversation).
                metaData(metaData).
                adminMembership(adminMembership).
                build();
    }


    public AddMemberResponse addMember(
            Principal principal,
            AddMemberRequest request) {
        /*
        Check admin role
        Check block
        Check already join group
        Check friend relation with admin
         */
        User admin = userRepository.findByUsername(principal.getName()).orElseThrow();
        User friend = userRepository.findById(request.getMemberId()).
                orElseThrow(UserNotFoundException::new);

        if(blockService.checkBlockstatus(admin.getId(), friend.getId())
                || blockService.checkBlockstatus(friend.getId(), admin.getId())) {
            throw new UserNotFoundException();
        }

        Membership newMembership = Membership.builder().
                        conversationId(request.getConversationId()).
                        userId(request.getMemberId()).
                        status(Membership_Status.PENDING).
                        build();

        if(friendService.checkFriendRelationship(admin.getId(), friend.getId())){
            newMembership.setStatus(Membership_Status.ACTIVE);
        }
        Membership savedMembership = membershipRepository.save(newMembership);

//        log.info("Added Member: {}", user);


        /*
        Send notification
         */
        GroupConversationDto newGroup = conversationRepository.getGroupById(
                request.getConversationId(), request.getMemberId()).getFirst();
        NewGroupNotification notification = NewGroupNotification.builder().
                newConversation(newGroup).notificationType(Notification_Type.NEW_CONVERSATION)
                .build();
        messagingTemplate.convertAndSendToUser(
                friend.getUsername(), "/queue/messages", notification);

//        Notification notification = Notification.builder().
//                notificationType(Notification_Type.ADDED_TO_A_GROUP).
//                userId(friend.getId()).
//                seenStatus(false).
//                notificationContent("Đã được thêm vào một nhóm mới").
//                build();
//        notificationService.sendNotification(notification, friend);


        return AddMemberResponse.builder().
                memberName(friend.getName()).
                memberId(friend.getId()).
                build();
    }

    public LeaveGroupResponse leaveGroup(
            Principal principal,
            LeaveGroupRequest request) {
        /*
        Check admin role
        Check already join group
        Check valid membership
         */

        User user = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        try{
            membershipRepository.deleteById(request.getMembershipId());
        } catch (Exception e) {
            log.info("Error: {}", e.getCause());
            throw new LeaveGroupException();
        }
        /*
        Send notification
         */

        return LeaveGroupResponse.builder().status("LEFT GROUP").build();
    }


    public DeleteMemberResponse deleteMember(
            Principal principal,
            DeleteMemberRequest request) {
        User admin = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        User removedMember = userRepository.findById(request.getMemberId()).orElseThrow(UserNotFoundException::new);
        /*
        Check valid data
        Check admin role
         */
        try{
            membershipRepository.deleteByConversationIdAndUserId(request.getGroupId(), request.getMemberId());
        } catch (Exception e) {
            log.info("Error: {}", e.getCause());
            throw new LeaveGroupException();
        }

        RemovedNotification notification = RemovedNotification.builder().
                groupId(request.getGroupId()).notificationType(Notification_Type.REMOVED_FROM_GROUP)
                .build();
        messagingTemplate.convertAndSendToUser(
                removedMember.getUsername(), "/queue/messages", notification);


        return DeleteMemberResponse.builder().status("DELETED").build();
    }


    public List<GetGroupMemberResponse> getMemberList(
            Principal principal,
            String conversationId) {
        /*
        Check valid data
         */

        return conversationRepository.getMemberList(conversationId);
    }




}
