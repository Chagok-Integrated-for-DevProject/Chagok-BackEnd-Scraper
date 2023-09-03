package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.domain.entitiy.SiteVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteVisitRepository extends JpaRepository<SiteVisit, Long> {

    Optional<SiteVisit> findByJobSiteType(JobSiteType jobSiteType);
}
