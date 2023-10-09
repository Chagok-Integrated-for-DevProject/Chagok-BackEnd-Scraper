package com.project.chagok.backend.scraper.batch.util;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public class BatchContextUtil {

    public static ExecutionContext getExecutionContextOfJob(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    public static ExecutionContext getExecutionContextOfJob(JobExecution jobExecution) {
        return jobExecution.getExecutionContext();
    }

    public static ExecutionContext getExecutionContextOfJob(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExecutionContext();
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

    public static Object getDataInContext(StepExecution stepExecution, String key) {
        ExecutionContext exc = getExecutionContextOfJob(stepExecution);
        return exc.get(key);
    }

    public static Object getDataInRunParam(JobExecution jobExecution, String key) {
       return jobExecution.getJobParameters().getParameter(key).getValue();
    }

    public static Object getDataInRunParam(ChunkContext chunkContext, String key) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getJobParameters().getParameter(key).getValue();
    }
}
