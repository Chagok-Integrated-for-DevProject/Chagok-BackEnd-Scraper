package com.project.chagok.backend.scraper.batch.config;

import com.project.chagok.backend.scraper.batch.listener.VisitorListener;
import com.project.chagok.backend.scraper.batch.processor.ProjectStudyItemProcessor;
import com.project.chagok.backend.scraper.batch.reader.scraper.HolaScraper;
import com.project.chagok.backend.scraper.batch.reader.scraper.LetsplScraper;
import com.project.chagok.backend.scraper.batch.sitevisit.LetsplVisitor;
import com.project.chagok.backend.scraper.batch.tasklet.LetsplBoardIdExtractor;
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
public class LetsplJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    @Qualifier("letsplJob")
    public Job letsplJob(@Qualifier("firstLetsplStep") Step firstStep, @Qualifier("secondLetsplChunkStep") Step secondStep, LetsplVisitor visitor) {
        return new JobBuilder("letsplJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new VisitorListener(visitor))
                .start(firstStep)
                .next(secondStep)
                .build();
    }

    @Bean
    @Qualifier("firstLetsplStep")
    public Step firstLetsplStep(LetsplBoardIdExtractor letsplBoardIdExtractor) {
        return new StepBuilder("firstLetsplStep", jobRepository)
                .tasklet(letsplBoardIdExtractor, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("secondLetsplChunkStep")
    public Step secondLetsplChunkStep(LetsplScraper letsplScraper, ProejctStudyItemWriter proejctStudyItemWriter, ProjectStudyItemProcessor projectStudyItemProcessor) {
        return new StepBuilder("secondLetsplChunkStep", jobRepository)
                .<StudyProjectDto, StudyProjectDto>chunk(3, transactionManager)
                .reader(letsplScraper)
                .processor(projectStudyItemProcessor)
                .writer(proejctStudyItemWriter)
                .faultTolerant()
                .skip(Throwable.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .build();
    }
}
