package coupon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class ReadOnlyDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isForcedMaster = RoutingContextHolder.isForcedMaster();
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        Object key;
        String reason;

        if (isForcedMaster) {
            key = DataSourceType.WRITER;
            reason = "Redis Forced Master Flag";
        } else if (isReadOnly) {
            key = DataSourceType.READER;
            reason = "@Transactional(readOnly = true)";
        } else {
            key = DataSourceType.WRITER;
            reason = "Default (Write Transaction)";
        }

        log.info(">>>> DB Routing | Target: [{}] | Reason: [{}] | Thread: [{}]",
                key, reason, Thread.currentThread().getName());

        return key;
    }
}
