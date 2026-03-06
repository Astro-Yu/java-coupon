package coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
@Slf4j
public class MasterRouteAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("execution(* coupon.CouponService.get*(..)) && args(.., userId)")
    public Object route(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
        log.info(">>>> AOP Check Redis for User: {}", userId); // 이 로그가 찍히는지 확인
        String key = "consistency:coupon:" + userId;

        Boolean hasKey = redisTemplate.hasKey(key);
        log.info(">>>> Redis Key: [{}], HasKey: [{}]", key, hasKey); // 이 로그가 중요합니다!

        if (redisTemplate.hasKey(key)) {
            RoutingContextHolder.setForcedMaster();
        }

        try {
            return joinPoint.proceed();
        } finally {
            RoutingContextHolder.clear();
        }
    }
}
