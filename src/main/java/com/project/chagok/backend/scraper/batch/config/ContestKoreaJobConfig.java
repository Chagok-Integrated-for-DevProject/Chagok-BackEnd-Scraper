package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.ScrapJobListener;
import com.project.chagok.backend.scraper.batch.reader.ContestKoreaItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.ContestKoreaTasklet;
import com.project.chagok.backend.scraper.batch.writer.ContestItemWriter;
import com.project.chagok.backend.scraper.dto.ContestDto;
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
public class ContestKoreaJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ScrapJobListener scrapJobListener;


    @Bean
    @Qualifier("contestKoreaJob")
    public Job contestJob(@Qualifier("firstContestStep") Step firstStep, @Qualifier("secondContestChunkStep") Step secondStep) {
        return new JobBuilder("contestKoreaJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(scrapJobListener)
                .start(firstStep)
                .next(secondStep)
                .build();
    }

    @Bean
    @Qualifier("firstContestKoreaStep")
    public Step firstContestStep(ContestKoreaTasklet contestKoreaTasklet) {
        return new StepBuilder("firstContestKoreaStep", jobRepository)
                .tasklet(contestKoreaTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("secondContestKoreaChunkStep")
    public Step secondContestChunkStep(ContestKoreaItemReader contestKoreaItemReader, ContestItemWriter contestItemWriter) {
        return new StepBuilder("secondContestKoreaChunkStep", jobRepository)
                .<ContestDto, ContestDto>chunk(3, transactionManager)
                .reader(contestKoreaItemReader)
                .writer(contestItemWriter)
                .build();
    }
}
