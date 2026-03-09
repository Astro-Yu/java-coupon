package coupon;

import lombok.RequiredArgsConstructor;
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

    public Coupon getCoupon(Long id) {
        return couponRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 쿠폰임"));
    }

    @Transactional
    public void updateDiscount(Long id, int discount) {
        Coupon coupon = couponRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 쿠폰임"));

        coupon.updateDiscountAmount(discount);
    }

    @Transactional
    public void updateMinimumOrder(Long id, int minimumOrder) {
        Coupon coupon = couponRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 쿠폰임"));

        coupon.updateMinimumOrderAmount(minimumOrder);
    }
}
