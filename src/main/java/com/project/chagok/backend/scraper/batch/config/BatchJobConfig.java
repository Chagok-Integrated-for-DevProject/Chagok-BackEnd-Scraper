package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.ScrapJobListener;
import com.project.chagok.backend.scraper.batch.processor.ProjectStudyItemProcessor;
import com.project.chagok.backend.scraper.batch.reader.ContestKoreaItemReader;
import com.project.chagok.backend.scraper.batch.reader.HolaItemReader;
import com.project.chagok.backend.scraper.batch.reader.InflearnItemReader;
import com.project.chagok.backend.scraper.batch.reader.OkkyItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.ContestKoreaTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.HolaUrlTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.InflearnTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.OkkyTasklet;
import com.project.chagok.backend.scraper.batch.writer.ContestItemWriter;
import com.project.chagok.backend.scraper.batch.writer.ProejctStudyItemWriter;
import com.project.chagok.backend.scraper.dto.ContestDto;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.skip.CompositeSkipPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ScrapJobListener scrapJobListener;

    @Bean
    @Qualifier("holaJob")
    public Job holaJob(@Qualifier("firstHolaStep") Step firstStep, @Qualifier("secondHolaChunkStep") Step secondStep) {
        return new JobBuilder("holaJob", jobRepository)
                .listener(scrapJobListener)
                .incrementer(new RunIdIncrementer())
                .start(firstStep)
                .next(secondStep)
                .build();
    }

    @Bean
    @Qualifier("firstHolaStep")
    public Step firstHolaStep(HolaUrlTasklet holaUrlTasklet) {
        return new StepBuilder("firstHolaStep", jobRepository)
                .tasklet(holaUrlTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("secondHolaChunkStep")
    public Step secondHolaChunkStep(HolaItemReader holaItemReader, ProejctStudyItemWriter proejctStudyItemWriter, ProjectStudyItemProcessor projectStudyItemProcessor) {
        return new StepBuilder("secondHolaChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(holaItemReader)
                .processor(projectStudyItemProcessor)
                .writer(proejctStudyItemWriter)
                .faultTolerant()
                .skip(Throwable.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .build();
    }


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
