package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.reader.ContestItemReader;
import com.project.chagok.backend.scraper.batch.reader.HolaItemReader;
import com.project.chagok.backend.scraper.batch.reader.InflearnItemReader;
import com.project.chagok.backend.scraper.batch.reader.OkkyItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.ContestTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.HolaUrlTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.InflearnTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.OkkyTasklet;
import com.project.chagok.backend.scraper.batch.writer.ContestItemWriter;
import com.project.chagok.backend.scraper.batch.writer.ProejctStudyItemWriter;
import com.project.chagok.backend.scraper.dto.ContestDto;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    @Qualifier("holaJob")
    public Job holaJob(@Qualifier("firstHolaStep") Step firstStep, @Qualifier("secondHolaChunkStep") Step secondStep) {
        return new JobBuilder("holaJob", jobRepository)
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
    public Step secondHolaChunkStep(HolaItemReader holaItemReader, ProejctStudyItemWriter proejctStudyItemWriter) {
        return new StepBuilder("secondHolaChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(holaItemReader)
                .writer(proejctStudyItemWriter)
                .build();
    }


    @Bean
    @Qualifier("okkyJob")
    public Job okkyJob(@Qualifier("firstOkkyStep") Step firstStep, @Qualifier("secondOkkyChunkStep") Step secondStep) {
        return new JobBuilder("okkyJob", jobRepository)
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
    public Step secondOkkyChunkStep(OkkyItemReader okkyItemReader, ProejctStudyItemWriter proejctStudyItemWriter) {
        return new StepBuilder("secondOkkyChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(okkyItemReader)
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
    public Step secondInflearnChunkStep(InflearnItemReader inflearnItemReader, ProejctStudyItemWriter proejctStudyItemWriter) {
        return new StepBuilder("secondOkkyChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(inflearnItemReader)
                .writer(proejctStudyItemWriter)
                .build();
    }


    @Bean
    @Qualifier("contestJob")
    public Job contestJob(@Qualifier("firstContestStep") Step firstStep, @Qualifier("secondContestChunkStep") Step secondStep) {
        return new JobBuilder("contestJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(firstStep)
                .next(secondStep)
                .build();
    }

    @Bean
    @Qualifier("firstContestStep")
    public Step firstContestStep(ContestTasklet contestTasklet) {
        return new StepBuilder("firstContestStep", jobRepository)
                .tasklet(contestTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("secondContestChunkStep")
    public Step secondContestChunkStep(ContestItemReader contestItemReader, ContestItemWriter contestItemWriter) {
        return new StepBuilder("secondContestChunkStep", jobRepository)
                .<ContestDto, ContestDto>chunk(3, transactionManager)
                .reader(contestItemReader)
                .writer(contestItemWriter)
                .build();
    }
}
