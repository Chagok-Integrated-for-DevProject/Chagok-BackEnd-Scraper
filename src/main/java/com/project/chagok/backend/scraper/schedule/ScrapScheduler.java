package com.project.chagok.backend.scraper.schedule;

import com.project.chagok.backend.scraper.batch.utils.BatchUtils;
import com.project.chagok.backend.scraper.constants.SiteType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScrapScheduler {

    private final Job holaJob;
    private final Job okkyJob;
    private final Job inflearnJob;
    private final Job contestJob;
    private final JobLauncher jobLauncher;

    public ScrapScheduler(@Qualifier("holaJob")Job holaJob, @Qualifier("okkyJob")Job okkyJob,
                          @Qualifier("inflearnJob")Job inflearnJob, @Qualifier("contestJob")Job contestJob, JobLauncher jobLauncher) {
        this.contestJob = contestJob;
        this.inflearnJob = inflearnJob;
        this.holaJob = holaJob;
        this.okkyJob = okkyJob;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 */3 * * * *")
    public void holaScrapScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(holaJob, BatchUtils.getJobParamWithTime(SiteType.HOLA));
    }

    @Scheduled(cron = "0 */3 * * * *")
    public void okkyScrapScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(okkyJob, BatchUtils.getJobParamWithTime(SiteType.OKKY));
    }

    @Scheduled(cron = "0 */3 * * * *")
    public void inflearnScrapScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(inflearnJob, BatchUtils.getJobParamWithTime(SiteType.INFLEARN));
    }

    @Scheduled(cron = "0 */3 * * * *")
    public void contestScrapScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        jobLauncher.run(contestJob, BatchUtils.getJobParamWithTime(SiteType.CONTEST_KOREA));
    }

}
