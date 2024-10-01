package JavaL5.chatApp.controller;


import JavaL5.chatApp.dto.channel.CreateChannelRequest;
import JavaL5.chatApp.dto.channel.CreateChannelResponse;
import JavaL5.chatApp.dto.channel.GetChannelResponse;
import JavaL5.chatApp.model.Channel;
import JavaL5.chatApp.service.ChannelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/channels")
public class ChannelController extends BaseController {


    @Autowired
    private ChannelService channelService;

    @PostMapping("/create")
    public ResponseEntity<CreateChannelResponse> createChannel(@Valid @RequestBody CreateChannelRequest request){
            Channel channel = channelService.createChannel(this.getAuthenticatedApp(), request);
            return ResponseEntity.ok(CreateChannelResponse.builder().channel(channel).build()) ;



    }

    /*
      Get Channel by ID
      -> Get messages of channel,
    */
    @GetMapping("{id}")
    public ResponseEntity<GetChannelResponse>  getChannelById(@PathVariable String id){
            Channel channel = channelService.getChannelById(id);
            return ResponseEntity.ok(GetChannelResponse.builder().channel(channel).build());

    }

//    @GetMapping("{clientChannelId}/by-reference-id")
//    public GetChannelResponse getChannelByReferenceId(@PathVariable String clientChannelId){
//
//    }
}
