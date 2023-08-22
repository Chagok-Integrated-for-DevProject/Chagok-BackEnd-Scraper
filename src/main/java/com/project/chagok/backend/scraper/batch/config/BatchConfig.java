package com.project.chagok.backend.scraper.batch.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.project.chagok.backend.scraper.batch.reader.HolaItemReader;
import com.project.chagok.backend.scraper.batch.reader.OkkyItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.HolaUrlTasklet;
import com.project.chagok.backend.scraper.batch.tasklet.OkkyTasklet;
import com.project.chagok.backend.scraper.batch.writer.ProejctStudyItemWriter;
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

}
