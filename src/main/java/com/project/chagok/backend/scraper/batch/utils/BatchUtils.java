package com.project.chagok.backend.scraper.batch.utils;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public class BatchUtils {

    public static final String PARSING_URL_KEY = "parsing url";

    public static ExecutionContext getExecutionContextOfJob(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    public static ExecutionContext getExecutionContextOfJob(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExecutionContext();
    }
}
