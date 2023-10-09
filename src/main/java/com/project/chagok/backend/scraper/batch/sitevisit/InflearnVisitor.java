package com.project.chagok.backend.scraper.batch.sitevisit;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Project;
import com.project.chagok.backend.scraper.domain.entitiy.Study;
import com.project.chagok.backend.scraper.domain.repository.ProjectRepository;
import com.project.chagok.backend.scraper.domain.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InflearnVisitor implements SiteVisitor{

    private final StudyRepository studyRepository;
    private final ProjectRepository projectRepository;
    private LocalDateTime visitTimeIdx;

    @Override
    public boolean isVisit(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVisit(LocalDateTime createdTime) {
        return visitTimeIdx.isAfter(createdTime) || visitTimeIdx.isEqual(createdTime);
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException();
    }

    public void studyInit() {
        Optional<Study> recentStudyData = studyRepository.findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType.INFLEARN);
        if (recentStudyData.isPresent())
            visitTimeIdx = recentStudyData.get().getCreatedTime();
        else
            visitTimeIdx = LocalDateTime.MIN;
    }

    public void projectInit() {
        Optional<Project> recentStudyData = projectRepository.findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType.INFLEARN);
        if (recentStudyData.isPresent())
            visitTimeIdx = recentStudyData.get().getCreatedTime();
        else
            visitTimeIdx = LocalDateTime.MIN;
    }

}
