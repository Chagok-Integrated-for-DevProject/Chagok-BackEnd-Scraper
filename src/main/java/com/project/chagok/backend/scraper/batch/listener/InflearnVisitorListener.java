package com.project.chagok.backend.scraper.batch.listener;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.sitevisit.InflearnVisitor;
import com.project.chagok.backend.scraper.batch.sitevisit.SiteVisitor;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import org.springframework.batch.core.JobExecution;

public class InflearnVisitorListener extends VisitorListener{
    public InflearnVisitorListener(SiteVisitor visitor) {
        super(visitor);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobSiteType jobSiteType = (JobSiteType) BatchContextUtil.getDataInRunParam(jobExecution, BatchUtil.SITE_TYPE_KEY);

        if (visitor instanceof InflearnVisitor) {
            if (jobSiteType == JobSiteType.INFLEARN_PROJECT) {
                ((InflearnVisitor) visitor).projectInit();
            } else if (jobSiteType == JobSiteType.INFLEARN_STUDY) {
                ((InflearnVisitor) visitor).studyInit();
            }
        }
    }
}
