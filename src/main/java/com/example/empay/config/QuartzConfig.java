package com.example.empay.config;

import com.example.empay.job.DeleteOldTransactionsJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Quartz.
 */
@Slf4j
@Configuration
public class QuartzConfig {

    /**s
     * Configuration of the cron trigger.
     */
    @Value("${jobs.deleteOldTransactions.cronTrigger:0 0 * * * ?}")
    private String deleteOldTransactionsJobCron;

    /**
     * Create job details for the Delete Old Transactions job.
     *
     * @return A JobDetail instance.
     */
    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(DeleteOldTransactionsJob.class)
                .storeDurably()
                .withIdentity("Delete_old_transactions")
                .withDescription("Delete transactions older than 1 hour")
                .build();
    }

    /**
     * Create a trigger for the Delete Old Transactions job.
     *
     * @param job The job detail.
     * @return The created trigger.
     */
    @Bean
    public Trigger trigger(final JobDetail job) {
        log.info("Cron trigger for delete old transactions quartz job: {}", deleteOldTransactionsJobCron);
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("delete_old_transactions_trigger")
                .withDescription("Delete old transactions job trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(deleteOldTransactionsJobCron))
                .build();
    }
}
