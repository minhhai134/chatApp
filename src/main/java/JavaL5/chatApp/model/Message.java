package JavaL5.chatApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "message")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /*
    Khong dung String channelId, String userId
    -> Thay vao do dung cac anotation sau
     */
//    @ManyToOne
//    @JoinColumn(name = "channelId", nullable = false)
//    @JsonIgnore()
//    private Channel channel;
    @Column(name = "channelId")
    private String channelId;

//    @ManyToOne
//    @JoinColumn(name = "userId", nullable = false)
//    @JsonIgnore()
//    private User user;
    @Column(name = "userId")
    private String userId;

    private String message;

    /*
    Su dung @CreationTimesStamp va Instant type
     */
    @CreationTimestamp
    private Instant sentTime;


    /*
    STT cua tin nhan trong cuoc tro chuyen
     */
//  private int serialNumber; -> Thu dung cach khac




}
