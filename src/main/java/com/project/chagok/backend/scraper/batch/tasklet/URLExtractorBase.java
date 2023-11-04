package com.project.chagok.backend.scraper.batch.tasklet;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.sitevisit.SiteVisitor;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class URLExtractorBase implements Tasklet {

    SiteVisitor visitor;
    public URLExtractorBase(SiteVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        JobSiteType siteType = getSiteType(chunkContext);

        List<String> willParseUrls = extractURL(siteType);

        saveURLInContext(chunkContext, willParseUrls);

        return RepeatStatus.FINISHED;
    }

    // 스크랩 하려는 사이트 type 초기화
    private JobSiteType getSiteType(ChunkContext chunkContext) {
        return (JobSiteType) BatchContextUtil.getDataInRunParam(chunkContext, BatchUtil.SITE_TYPE_KEY);
    }

    // job execution context에 파싱할 url 저장
    private void saveURLInContext(ChunkContext chunkContext, List<String> willParseUrls) {
        BatchContextUtil.saveDataInContext(chunkContext, BatchUtil.SITE_URLS, willParseUrls);
    }

    abstract List<String> extractURL(JobSiteType jobSiteType) throws Exception;

    public boolean isVisit(LocalDateTime localDateTime) {
        return visitor.isVisit(localDateTime);
    }

    public boolean isVisit(String url) {
        return visitor.isVisit(url);
    }
}
