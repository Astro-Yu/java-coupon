package coupon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class ReadOnlyDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            log.info("🚩 현재 선택된 라우팅 키: {}", "SLAVE");
            return DataSourceType.READER;
        }
        log.info("🚩 현재 선택된 라우팅 키: {}", "MASTER");
        return DataSourceType.WRITER;
    }
}
