package coupon;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonLockFacade {

    private final RedissonClient redissonClient;
    private final CouponService couponService;

    public void updateDiscount(Long id, int amount) {
        executeWithLock(id, () -> couponService.updateDiscount(id, amount));
    }

    public void updateMinimumOrder(Long id, int amount) {
        executeWithLock(id, () -> couponService.updateMinimumOrder(id, amount));
    }

    private void executeWithLock(Long id, Runnable logic) {
        RLock lock = redissonClient.getLock("coupon_lock:" + id);

        try {
            // 1. 락 획득 시도 (최대 5초 대기, 락 점유 1초)
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS);

            if (!available) {
                throw new RuntimeException("락 획득 실패: " + id);
            }

            logic.run();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
