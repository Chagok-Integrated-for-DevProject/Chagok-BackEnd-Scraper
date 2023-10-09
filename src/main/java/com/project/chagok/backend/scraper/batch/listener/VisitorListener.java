package com.project.chagok.backend.scraper.batch.listener;

import com.project.chagok.backend.scraper.batch.sitevisit.SiteVisitor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class VisitorListener implements JobExecutionListener {

    SiteVisitor visitor;

    public VisitorListener(SiteVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        visitor.init();
    }
}
