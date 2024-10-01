package JavaL5.chatApp.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "channel")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String clientChannelId;

    @Column(name = "appId")
    private String appId;

    private String channelName;

    private int messageQuantity=0;
}
