package JavaL5.chatApp.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "app")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String clientAppId;

    private String appName;

    @Column(name = "appApiKey", unique = true)
    private String appApiKey;
}
