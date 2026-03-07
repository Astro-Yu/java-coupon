package coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public void create(Coupon coupon) {
        couponRepository.save(coupon);
    }

    // 전략: Look-aside. 쿠폰 ID를 키로 캐싱.
    // 이유: 쿠폰 상세 정보는 자주 바뀌지 않으며, 여러 유저가 공통으로 참조함.
    @Cacheable(value = "coupons", key = "#id")
    public Coupon getCoupon(Long id) {
        return couponRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 쿠폰임"));
    }

    @CacheEvict(value = "coupons", key = "#id")
    @Transactional
    public void updateCoupon(Long id, String newName) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 없음"));
        coupon.updateName(newName);
    }
}
