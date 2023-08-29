package com.project.chagok.backend.scraper.batch.domain.repository;

import com.project.chagok.backend.scraper.batch.domain.entitiy.SiteVisit;
import com.project.chagok.backend.scraper.constants.SiteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteVisitRepository extends JpaRepository<SiteVisit, Long> {

    Optional<SiteVisit> findBySiteType(SiteType siteType);
}
