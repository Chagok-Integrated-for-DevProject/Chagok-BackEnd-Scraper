package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.ScrapJobListener;
import com.project.chagok.backend.scraper.batch.processor.ProjectStudyItemProcessor;
import com.project.chagok.backend.scraper.batch.reader.HolaItemReader;
import com.project.chagok.backend.scraper.batch.tasklet.HolaUrlTasklet;
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
public class HolaJobConfig {

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
}
