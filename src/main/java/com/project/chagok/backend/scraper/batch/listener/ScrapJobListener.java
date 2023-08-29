package com.project.chagok.backend.scraper.batch.listener;

import com.project.chagok.backend.scraper.batch.domain.entitiy.SiteVisit;
import com.project.chagok.backend.scraper.batch.domain.repository.SiteVisitRepository;
import com.project.chagok.backend.scraper.batch.utils.BatchUtils;
import com.project.chagok.backend.scraper.constants.SiteType;
import jakarta.persistence.EntityNotFoundException;
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
        SiteType siteType = (SiteType) jobExecution.getJobParameters().getParameter(BatchUtils.SITE_TYPE_KEY).getValue();

        Optional<SiteVisit> savedSiteVisit = siteVisitRepository.findBySiteType(siteType);

        Long visitIdx = 0L;
        if (savedSiteVisit.isPresent()) { // 처음 저장하는게 아니라면,
            visitIdx = savedSiteVisit.get().getVisitIdx();
        }

        String idxKey = null;
        switch (siteType) {
            case HOLA -> idxKey = BatchUtils.HOLA_VISIT_IDX_KEY;
            case OKKY -> idxKey = BatchUtils.OKKY_VISIT_IDX_KEY;
        }

        BatchUtils.saveDataInContext(jobExecution, idxKey, visitIdx);
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {

        // job 끝나면, visit index 저장


        SiteType siteType = (SiteType) jobExecution.getJobParameters().getParameter(BatchUtils.SITE_TYPE_KEY).getValue();

        String idxKey = null;
        switch (siteType) {
            case HOLA -> idxKey = BatchUtils.HOLA_VISIT_IDX_KEY;
            case OKKY -> idxKey = BatchUtils.OKKY_VISIT_IDX_KEY;
        }

        Optional<SiteVisit> savedSiteVisit = siteVisitRepository.findBySiteType(siteType);

        if (savedSiteVisit.isEmpty()) { // DB에 저장는게 없다면,
            siteVisitRepository.save(new SiteVisit(siteType, (Long) BatchUtils.getDataInContext(jobExecution, idxKey)));
        } else { // 저장되어있을 시, 업데이트
            // 사이트 별 visit index 저장
            SiteVisit siteVisit = savedSiteVisit.get();

            siteVisit.updateVisitIdx((Long) BatchUtils.getDataInContext(jobExecution, idxKey));
        }
    }
}
