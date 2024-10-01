package JavaL5.chatApp.aop.ratelimiter;

import JavaL5.chatApp.common.AuthenticationFilter;
import JavaL5.chatApp.model.App;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@Slf4j
public class RateLimiterAspect {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(RateLimit)")
    public Object RateLimiter(ProceedingJoinPoint joinPoint) throws Throwable{
         App app = (App) request.getAttribute(AuthenticationFilter.APP_ATTRIBUTE);
         String api_key = app.getAppApiKey();

         // Extract the method signature
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        int limit = rateLimit.limit();
        int timeWindow = rateLimit.timeWindow();

        boolean limitExceeded = true;

        for(int i=1;i<=limit;i++){
            String key = String.format("%s:%d",api_key,i);
            if(redisTemplate.getExpire(key)<0) {
                redisTemplate.opsForValue().set(key, key, Duration.ofSeconds(timeWindow));
                log.info("Redis - Request - {}", i);
                limitExceeded = false;
                break;
            }
        }

        if(limitExceeded) throw new RateLimitExceededException();

        return joinPoint.proceed();

    }
}
