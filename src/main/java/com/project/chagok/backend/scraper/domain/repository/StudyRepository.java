package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.domain.entitiy.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

}
