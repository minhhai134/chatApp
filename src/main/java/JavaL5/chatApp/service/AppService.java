package JavaL5.chatApp.service;


import JavaL5.chatApp.model.App;
import org.springframework.stereotype.Service;

@Service
public interface AppService {

    public App getAppByAppApiKey(String appApiKey);



}
