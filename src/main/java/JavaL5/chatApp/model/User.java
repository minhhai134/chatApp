package JavaL5.chatApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "user_data")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    @ManyToOne
//    @JoinColumn(name = "appId", nullable = false)
//    @JsonIgnore
//    private App app;

    @Column(name = "appId")
    private String appId;

    private String clientUserId;

    private String userName;
}
