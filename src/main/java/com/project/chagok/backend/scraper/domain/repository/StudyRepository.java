package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    Optional<Study> findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType siteType);
}
