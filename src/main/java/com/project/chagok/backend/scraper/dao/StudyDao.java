package com.project.chagok.backend.scraper.dao;

import com.project.chagok.backend.scraper.domain.entitiy.Study;
import com.project.chagok.backend.scraper.domain.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyDao {

    private final StudyRepository studyRepository;

    public void save(Study study) {
        studyRepository.save(study);
    }
}
