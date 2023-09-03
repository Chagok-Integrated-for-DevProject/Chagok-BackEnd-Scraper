package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.domain.entitiy.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

}
