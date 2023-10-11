package com.project.chagok.backend.scraper.domain.repository;

import com.project.chagok.backend.scraper.domain.entitiy.Contest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    @Query("select c from Contest c where c.endDate > current_timestamp")
    List<Contest> findAllOpenContests();
}
