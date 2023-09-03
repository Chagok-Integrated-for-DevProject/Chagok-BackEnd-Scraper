package com.project.chagok.backend.scraper.dao;

import com.project.chagok.backend.scraper.domain.entitiy.Project;
import com.project.chagok.backend.scraper.domain.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProjectDao {

    private final ProjectRepository projectRepository;

    @Transactional
    public void save(Project project) {
        projectRepository.save(project);
    }
}
