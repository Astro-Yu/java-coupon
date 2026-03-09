package coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@RequiredArgsConstructor
@Getter
@DynamicUpdate
public class Coupon {

    private static final int MIN_DISCOUNT_RATE = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int discountAmount;

    private int minimumOrderAmount;

    private String category;

    private LocalDateTime publishedAt;

    private LocalDateTime expiredAt;

    @Version
    private Long version;
    
    public Coupon(int discountAmount, int minimumOrderAmount) {
        this.discountAmount = discountAmount;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public void updateMinimumOrderAmount(int newMinimumOrderAmount) {
        validateDiscountRatePolicy(discountAmount, newMinimumOrderAmount);
        this.minimumOrderAmount = newMinimumOrderAmount;
    }

    public void updateDiscountAmount(int newDiscountAmount) {
        validateDiscountRatePolicy(newDiscountAmount, minimumOrderAmount);
        this.discountAmount = newDiscountAmount;
    }

    private void validateDiscountRatePolicy(int discountAmount, int minimumOrderAmount) {
        int discountRate = (discountAmount * 100) / minimumOrderAmount;

        if (discountRate < MIN_DISCOUNT_RATE) {
            throw new IllegalStateException(String.format("할인률 위반임: %d", discountRate));
        }
    }
}
