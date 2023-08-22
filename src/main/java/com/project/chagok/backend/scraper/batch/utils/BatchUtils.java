package com.project.chagok.backend.scraper.batch.utils;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public class BatchUtils {

    public static final String HOLA_PARSING_URL_KEY = "hola_parsing_url";
    public static final String OKKY_PARSING_URL_KEY = "okky_parsing_url";
    public static final String INF_PARSING_URL_KEY = "inf_parsing_url";

    public static ExecutionContext getExecutionContextOfJob(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    public static ExecutionContext getExecutionContextOfJob(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExecutionContext();
    }
}
