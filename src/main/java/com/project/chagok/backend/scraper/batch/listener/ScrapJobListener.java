package com.project.chagok.backend.scraper.batch.listener;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.constants.VisitIdxKey;
import com.project.chagok.backend.scraper.batch.domain.entitiy.SiteVisit;
import com.project.chagok.backend.scraper.batch.domain.repository.SiteVisitRepository;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import com.project.chagok.backend.scraper.constants.SiteType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScrapJobListener implements JobExecutionListener {

    private final SiteVisitRepository siteVisitRepository;

    @Override
    @Transactional(readOnly = true)
    public void beforeJob(JobExecution jobExecution) {
        // job 시작전, DB에서, visit index 조회, visit index를 job context로 넣음
        JobSiteType jobSiteType = (JobSiteType) BatchContextUtil.getDataInRunParam(jobExecution, BatchUtil.SITE_TYPE_KEY);

        Optional<SiteVisit> savedSiteVisit = siteVisitRepository.findByJobSiteType(jobSiteType);

        Long visitIdx = 0L;
        if (savedSiteVisit.isPresent()) { // 처음 저장하는게 아니라면,
            visitIdx = savedSiteVisit.get().getVisitIdx();
        }

        String idxKey = null;
        switch (jobSiteType) {
            case HOLA -> idxKey = VisitIdxKey.HOLA.getKey();
            case OKKY -> idxKey = VisitIdxKey.OKKY.getKey();
            case INFLEARN_STUDY -> idxKey = VisitIdxKey.INFLEARN_STUDY.getKey();
            case INFLEARN_PROJECT -> idxKey = VisitIdxKey.INFLEARN_PROJECT.getKey();
        }

        BatchContextUtil.saveDataInContext(jobExecution, idxKey, visitIdx);
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {

        // job 끝나면, visit index 저장
        JobSiteType jobSiteType = (JobSiteType) jobExecution.getJobParameters().getParameter(BatchUtil.SITE_TYPE_KEY).getValue();

        String idxKey = null;
        switch (jobSiteType) {
            case HOLA -> idxKey = VisitIdxKey.HOLA.getKey();
            case OKKY -> idxKey = VisitIdxKey.OKKY.getKey();
            case INFLEARN_STUDY -> idxKey = VisitIdxKey.INFLEARN_STUDY.getKey();
            case INFLEARN_PROJECT -> idxKey = VisitIdxKey.INFLEARN_PROJECT.getKey();
        }

        Optional<SiteVisit> savedSiteVisit = siteVisitRepository.findByJobSiteType(jobSiteType);

        if (savedSiteVisit.isEmpty()) { // DB에 저장는게 없다면,
            siteVisitRepository.save(new SiteVisit(jobSiteType, (Long) BatchContextUtil.getDataInContext(jobExecution, idxKey)));
        } else { // 저장되어있을 시, 업데이트
            // 사이트 별 visit index 저장
            SiteVisit siteVisit = savedSiteVisit.get();

            siteVisit.updateVisitIdx((Long) BatchContextUtil.getDataInContext(jobExecution, idxKey));
        }
    }
}
