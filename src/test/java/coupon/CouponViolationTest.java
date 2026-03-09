package coupon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.persistence.EntityManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CouponViolationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EntityManager em;

    @Test
    void 동시_수정_재현_테스트() throws InterruptedException {
        // 1. 초기 데이터 저장: 4,000원 / 100,000원 (4%)
        Coupon coupon = couponRepository.save(new Coupon(4000, 100000));
        Long id = coupon.getId();

        // 영속성 컨텍스트를 비워서 스레드 X가 캐시된 객체를 쓰지 못하게 함
        em.clear();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // 스레드 A: 할인액 3,000원으로 변경 시도 (현재 100,000원 기준 3%라 통과 예상)
        executorService.submit(() -> {
            try {
                couponService.updateDiscount(id, 3_000);
            } finally {
                latch.countDown();
            }
        });

        // 스레드 B: 최소주문액 120,000원으로 변경 시도 (현재 4,000원 기준 3.3%라 통과 예상)
        executorService.submit(() -> {
            try {
                couponService.updateMinimumOrder(id, 120_000);
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // 마지막 조회 전에도 다시 한 번 비워줌 (스레드 A, B의 변경사항을 새로 읽기 위해)
        em.clear();

        System.out.printf("찾으려는 쿠폰 id: %d%n", id);
        Thread.sleep(2000);
        Coupon result = couponRepository.findById(id).orElseThrow();
        int finalRate = (result.getDiscountAmount() * 100) / result.getMinimumOrderAmount();

        System.out.println("최종 할인 금액: " + result.getDiscountAmount());
        System.out.println("최종 최소 주문: " + result.getMinimumOrderAmount());
        System.out.println("최종 할인율: " + finalRate + "%");

        assertThat(finalRate).isLessThan(3);
    }
}
