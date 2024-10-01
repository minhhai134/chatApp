package JavaL5.chatApp.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;

@Entity
@Table(name = "channelMembers")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class ChannelMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String channelMemberId;

    private String channelId;

    private String userId;

    private Date lastSeen;
}
