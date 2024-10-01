package JavaL5.chatApp.dto.channel;


import JavaL5.chatApp.model.Channel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetChannelResponse {
     private Channel channel;
}
