package coupon;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findById(Long id);
}
