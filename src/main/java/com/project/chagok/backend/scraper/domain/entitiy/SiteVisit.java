package com.project.chagok.backend.scraper.domain.entitiy;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.constants.SiteType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SiteVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    private JobSiteType jobSiteType;

    private Long visitIdx;

    public SiteVisit(JobSiteType jobSiteType, Long visitIdx) {
        this.jobSiteType = jobSiteType;
        this.visitIdx = visitIdx;
    }

    public void updateVisitIdx(Long visitIdx) {
        this.visitIdx = visitIdx;
    }
}
