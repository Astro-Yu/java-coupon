package coupon;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void create(Coupon coupon, Long userId) {
        couponRepository.save(coupon); // Master DB에 저장

        // Redis에 2초간 '최근 쓰기' 플래그 저장
        redisTemplate.opsForValue().set("consistency:coupon:" + userId, "Y", 2, TimeUnit.SECONDS);
    }

    public Coupon getCoupon(Long id, Long userId) {
        return couponRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 쿠폰임"));
    }
}
