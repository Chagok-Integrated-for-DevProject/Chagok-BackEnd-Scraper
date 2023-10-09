package com.project.chagok.backend.scraper.batch.sitevisit;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Study;
import com.project.chagok.backend.scraper.domain.repository.StudyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OkkyVisitor implements SiteVisitor{

    private final StudyRepository studyRepository;
    private LocalDateTime visitTimeIdx;

    @Override
    public void init() {
        Optional<Study> recentStudyData = studyRepository.findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType.OKKY);
        if (recentStudyData.isPresent())
            visitTimeIdx = recentStudyData.get().getCreatedTime();
        else
            visitTimeIdx = LocalDateTime.MIN;
    }

    @Override
    public boolean isVisit(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVisit(LocalDateTime createdTime) {
        return visitTimeIdx.isAfter(createdTime) || visitTimeIdx.isEqual(createdTime);
    }
}
