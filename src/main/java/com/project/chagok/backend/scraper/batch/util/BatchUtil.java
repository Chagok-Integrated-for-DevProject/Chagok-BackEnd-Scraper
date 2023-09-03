package com.project.chagok.backend.scraper.batch.util;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.constants.SiteType;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

public class BatchUtil {

    public static final String SITE_TYPE_KEY = "site_type_key";

    public static JobParameters getJobParamWithTime(JobSiteType jobSiteType) {

        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addJobParameter(SITE_TYPE_KEY, jobSiteType, JobSiteType.class)
                .toJobParameters();
    }
}
