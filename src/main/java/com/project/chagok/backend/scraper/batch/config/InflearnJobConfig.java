package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.InflearnVisitorListener;
import com.project.chagok.backend.scraper.batch.listener.VisitorListener;
import com.project.chagok.backend.scraper.batch.processor.ProjectStudyItemProcessor;
import com.project.chagok.backend.scraper.batch.reader.InflearnItemReader;
import com.project.chagok.backend.scraper.batch.sitevisit.InflearnVisitor;
import com.project.chagok.backend.scraper.batch.tasklet.InflearnURLExtractor;
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

    @Bean
    @Qualifier("inflearnJob")
    public Job inflearnJob(@Qualifier("firstInflearnStep") Step firstStep, @Qualifier("secondInflearnChunkStep") Step secondStep, InflearnVisitor visitor) {
        return new JobBuilder("inflearnJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new InflearnVisitorListener(visitor))
                .start(firstStep)
                .next(secondStep)
                .build();
    }


    @Bean
    @Qualifier("firstInflearnStep")
    public Step firstInflearnStep(InflearnURLExtractor inflearnURLExtractor) {
        return new StepBuilder("firstInflearnStep", jobRepository)
                .tasklet(inflearnURLExtractor, transactionManager)
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
