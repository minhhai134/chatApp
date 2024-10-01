package JavaL5.chatApp.common;

import JavaL5.chatApp.model.App;
import JavaL5.chatApp.service.AppService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/*
  DUNG CHO MOI REQUEST DEN CHUONG TRINH, DE XAC THUC APP CO QUYEN SU DUNG DICH VU KHONG,
  THONG QUA KIEM TRA HEADER
 */
public class AuthenticationFilter implements Filter {

    public static final String APP_ATTRIBUTE = "authenticatedApp";
    public static final String AUTH_HEADER = "x-api-key";

    public AuthenticationFilter(AppService appService) {
        this.appService = appService;
    }

    private AppService appService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        String appApiKey = request.getHeader(AUTH_HEADER);

        if(appApiKey==null){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        App app = appService.getAppByAppApiKey(request.getHeader(AUTH_HEADER));

        if(app==null){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        request.setAttribute(APP_ATTRIBUTE,app);

        filterChain.doFilter(request,response);




    }

    @Override
    public  void destroy(){

    }
}
