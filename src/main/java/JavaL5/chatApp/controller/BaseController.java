package JavaL5.chatApp.controller;

import JavaL5.chatApp.common.AuthenticationFilter;
import JavaL5.chatApp.model.App;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    @Autowired
    protected HttpServletRequest request;


    /*
    Buoc nay duoc thuc hien sau khi request da di qua Filter, da duoc authenticate
    */
    protected App getAuthenticatedApp(){
        return (App) request.getAttribute(AuthenticationFilter.APP_ATTRIBUTE);
    }

}
