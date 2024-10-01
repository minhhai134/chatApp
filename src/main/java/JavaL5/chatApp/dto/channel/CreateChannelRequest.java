package JavaL5.chatApp.dto.channel;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateChannelRequest {

    @NotEmpty
    private String channelName;

//    @NotEmpty
//    private String appId;
    // -> app luon duoc authenticate nen neu auth thanh cong thi khong can cung cap appId

    @NotEmpty
    private String clientChannelId;

}
