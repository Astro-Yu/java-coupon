package coupon;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    int countByCouponIdAndUserId(Long couponId, Long userId);

    List<UserCoupon> findAllByUserId(Long userId);
}
