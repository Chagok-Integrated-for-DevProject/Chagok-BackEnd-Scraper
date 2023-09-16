package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.ScrapJobListener;
import com.project.chagok.backend.scraper.batch.processor.ProjectStudyItemProcessor;
import com.project.chagok.backend.scraper.batch.reader.OkkyItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.OkkyTasklet;
import com.project.chagok.backend.scraper.batch.writer.ProejctStudyItemWriter;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class OkkyJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ScrapJobListener scrapJobListener;

    @Bean
    @Qualifier("okkyJob")
    public Job okkyJob(@Qualifier("firstOkkyStep") Step firstStep, @Qualifier("secondOkkyChunkStep") Step secondStep) {
        return new JobBuilder("okkyJob", jobRepository)
                .listener(scrapJobListener)
                .incrementer(new RunIdIncrementer())
                .start(firstStep)
                .next(secondStep)
                .build();
    }

    @Bean
    @Qualifier("firstOkkyStep")
    public Step firstOkkyStep(OkkyTasklet okkyTasklet) {
        return new StepBuilder("firstOkkyStep", jobRepository)
                .tasklet(okkyTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("secondOkkyChunkStep")
    public Step secondOkkyChunkStep(OkkyItemReader okkyItemReader, ProejctStudyItemWriter proejctStudyItemWriter, ProjectStudyItemProcessor projectStudyItemProcessor) {
        return new StepBuilder("secondOkkyChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(okkyItemReader)
                .processor(projectStudyItemProcessor)
                .writer(proejctStudyItemWriter)
                .faultTolerant()
                .skip(Throwable.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .build();
    }
}

