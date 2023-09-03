package com.project.chagok.backend.scraper.dao;

import com.project.chagok.backend.scraper.domain.entitiy.Contest;
import com.project.chagok.backend.scraper.domain.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ContestDao {

    private final ContestRepository contestRepository;

    @Transactional
    public void save(Contest contest) {
        contestRepository.save(contest);
    }
}
