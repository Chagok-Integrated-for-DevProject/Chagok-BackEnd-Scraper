package com.project.chagok.backend.scraper.batch.sitevisit;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Project;
import com.project.chagok.backend.scraper.domain.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HolaVisitor implements SiteVisitor{

    private final ProjectRepository projectRepository;

    private LocalDateTime visitTimeIdx;

    @Override
    public void init() {
        Optional<Project> newProjectStudyData = projectRepository.findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType.HOLA.name());
        if (newProjectStudyData.isPresent())
            visitTimeIdx = newProjectStudyData.get().getCreatedTime();
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
