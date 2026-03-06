package coupon;

public class RoutingContextHolder {
    private static final ThreadLocal<Boolean> FORCED_MASTER = new ThreadLocal<>();

    public static void setForcedMaster() {
        FORCED_MASTER.set(true);
    }

    public static boolean isForcedMaster() {
        return FORCED_MASTER.get() != null && FORCED_MASTER.get();
    }

    public static void clear() {
        FORCED_MASTER.remove();
    }
}
