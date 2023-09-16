package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.ScrapJobListener;
import com.project.chagok.backend.scraper.batch.processor.ProjectStudyItemProcessor;
import com.project.chagok.backend.scraper.batch.reader.InflearnItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.InflearnTasklet;
import com.project.chagok.backend.scraper.batch.writer.ProejctStudyItemWriter;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class InflearnJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ScrapJobListener scrapJobListener;

    @Bean
    @Qualifier("inflearnJob")
    public Job inflearnJob(@Qualifier("firstInflearnStep") Step firstStep, @Qualifier("secondInflearnChunkStep") Step secondStep) {
        return new JobBuilder("inflearnJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(scrapJobListener)
                .start(firstStep)
                .next(secondStep)
                .build();
    }


    @Bean
    @Qualifier("firstInflearnStep")
    public Step firstInflearnStep(InflearnTasklet inflearnTasklet) {
        return new StepBuilder("firstInflearnStep", jobRepository)
                .tasklet(inflearnTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("secondInflearnChunkStep")
    public Step secondInflearnChunkStep(InflearnItemReader inflearnItemReader, ProejctStudyItemWriter proejctStudyItemWriter, ProjectStudyItemProcessor projectStudyItemProcessor) {
        return new StepBuilder("secondOkkyChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(inflearnItemReader)
                .processor(projectStudyItemProcessor)
                .writer(proejctStudyItemWriter)
                .build();
    }
}
