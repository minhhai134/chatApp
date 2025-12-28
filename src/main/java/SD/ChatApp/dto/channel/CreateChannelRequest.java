package SD.ChatApp.dto.channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChannelRequest {
    
    @NotBlank(message = "Channel name is required")
    @Size(min = 1, max = 100, message = "Channel name must be between 1 and 100 characters")
    private String channelName;
    
    private String channelDescription;
}

