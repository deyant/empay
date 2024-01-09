package com.example.empay.job;

import com.example.empay.service.transaction.TransactionService;
import com.example.empay.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * This Quartz job deletes all transactions older than a certain period in hours.
 */
@Component
@Slf4j
@DisallowConcurrentExecution
public class DeleteOldTransactionsJob implements Job {

    /**
     * Transaction service.
     */
    @Autowired
    private TransactionService transactionService;

    /**
     * Maximum age in hours of transactions. All transactions older than this age
     * will be deleted. Default value is 1 hour.
     */
    @Value("${jobs.deleteOldTransactions.maxAgeInHours:1}")
    private Integer maxAgeInHours;

    /**
     * Called by a {@link org.quartz.Scheduler}.
     *
     * @param context The job context.
     * @throws JobExecutionException If an exception during job execution occurs.
     */
    @Transactional
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        ZonedDateTime dateBefore = ZonedDateTime.now(Constants.ZONE_ID_UTC).minus(maxAgeInHours, ChronoUnit.HOURS);
        try {
            int deletedCount = transactionService.deleteOldTransactions(dateBefore);
            log.info("Deleted {} transactions created before {}", deletedCount, dateBefore);
        } catch (Exception e) {
            log.error("Error while deleting transactions older than {}", dateBefore, e);
            throw new JobExecutionException("Error while deleting old transactions", e);
        }
    }

}
