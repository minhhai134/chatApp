package JavaL5.chatApp.controller;


import JavaL5.chatApp.aop.ratelimiter.RateLimit;
import JavaL5.chatApp.dto.app.InspectAppResponse;
import JavaL5.chatApp.model.App;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AppController extends BaseController {

    @GetMapping("/inspect")
    @RateLimit(limit =2)
    public ResponseEntity<InspectAppResponse> inspectApp(){ // parameter: (HttpServletRequest request)
        App app = this.getAuthenticatedApp();
        log.info("Class = {}, App = {}", app.getClass(), app);
        return ResponseEntity.ok(InspectAppResponse.builder().app(app).build());
    }
}
