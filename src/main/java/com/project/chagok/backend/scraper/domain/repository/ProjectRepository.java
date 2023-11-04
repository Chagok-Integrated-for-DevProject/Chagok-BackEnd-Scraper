package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {


    @Query(value = "SELECT * FROM project p WHERE site_type = :siteType \n" +
            "UNION ALL\n" +
            "SELECT * FROM study s WHERE site_type = :siteType ORDER BY created_time DESC\n" +
            "LIMIT 1;", nativeQuery = true)
    Optional<Project> findFirstBySiteTypeOrderByCreatedTimeDesc(@Param("siteType") String siteType);

    Optional<Project> findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType siteType);

    List<Project> findBySiteType(SiteType siteType);
}
