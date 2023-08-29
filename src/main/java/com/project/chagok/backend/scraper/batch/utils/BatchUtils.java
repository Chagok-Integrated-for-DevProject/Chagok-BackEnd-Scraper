package com.project.chagok.backend.scraper.batch.utils;

import com.project.chagok.backend.scraper.constants.SiteType;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.JobContext;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class BatchUtils {

    public static final String HOLA_PARSING_URL_KEY = "hola_parsing_url";
    public static final String OKKY_PARSING_URL_KEY = "okky_parsing_url";
    public static final String INF_PARSING_URL_KEY = "inf_parsing_url";
    public static final String CONTEST_PARSING_URL_KEY = "contest_parsing_url";

    public static final String HOLA_VISIT_IDX_KEY = "hola_visit_idx_key";
    public static final String OKKY_VISIT_IDX_KEY = "okky_visit_idx_key";

    public static final String SITE_TYPE_KEY = "site_type_key";

    public static ExecutionContext getExecutionContextOfJob(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    public static ExecutionContext getExecutionContextOfJob(JobExecution jobExecution) {
        return jobExecution.getExecutionContext();
    }

    public static ExecutionContext getExecutionContextOfJob(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExecutionContext();
    }

    public static JobParameters getJobParamWithTime(SiteType siteType) { // 테스트 코드

        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addJobParameter(SITE_TYPE_KEY, siteType, SiteType.class)
                .toJobParameters();
    }


    public static <T> void saveDataInContext(ChunkContext chunkContext, String key, T value) {

        ExecutionContext exc = getExecutionContextOfJob(chunkContext);
        exc.put(key, value);
    }

    public static <T> void saveDataInContext(JobExecution jobExecution, String key, T value) {

        ExecutionContext exc = getExecutionContextOfJob(jobExecution);
        exc.put(key, value);
    }

    public static Object getDataInContext(ChunkContext chunkContext, String key) {
        ExecutionContext exc = getExecutionContextOfJob(chunkContext);
        return exc.get(key);
    }

    public static Object getDataInContext(JobExecution jobExecution, String key) {
        ExecutionContext exc = getExecutionContextOfJob(jobExecution);

        return exc.get(key);
    }

}
