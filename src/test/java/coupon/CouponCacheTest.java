package coupon;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CouponCacheTest {
    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void cacheTest() {
        // 1. 데이터 준비 (DB에 쿠폰 하나 저장)
        Coupon coupon = couponRepository.save(new Coupon("할인 쿠폰"));
        Long userId = 100L;

        // 2. 쿠폰 발급 (5장 제한 테스트 포함)
        for (int i = 0; i < 5; i++) {
            userCouponService.createUserCoupon(userId, coupon.getId());
        }

        // 6번째 발급 시도 시 예외 발생 확인 (기능 요구사항 1번)
        assertThrows(IllegalStateException.class, () ->
                userCouponService.createUserCoupon(userId, coupon.getId())
        );
        
        entityManager.flush();
        entityManager.clear();

        // 3. 목록 조회 (기능 요구사항 2, 3번)
        System.out.println("=== 1차 조회 (DB 쿼리 발생해야 함) ===");
        userCouponService.getUserCoupons(userId);

        System.out.println("=== 2차 조회 (DB 쿼리 없이 Redis에서 가져와야 함) ===");
        userCouponService.getUserCoupons(userId);
    }
}

