package coupon;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    private final CouponService couponService;

    @Transactional
    public void createUserCoupon(Long userId, Long couponId) {
        int userCouponCount = userCouponRepository.countByCouponIdAndUserId(couponId, userId);

        if (userCouponCount >= 5) {
            throw new IllegalStateException("더 이상 쿠폰을 발급할 수 없삼");
        }

        Coupon coupon = couponService.getCoupon(couponId);
        UserCoupon userCoupon = new UserCoupon(userId, coupon.getId());

        userCouponRepository.save(userCoupon);
    }

    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.findAllByUserId(userId);
    }
}
