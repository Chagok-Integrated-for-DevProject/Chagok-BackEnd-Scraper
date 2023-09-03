package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.domain.entitiy.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

}
