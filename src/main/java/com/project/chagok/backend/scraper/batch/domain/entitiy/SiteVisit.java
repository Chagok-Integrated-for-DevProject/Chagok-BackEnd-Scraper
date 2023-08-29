package com.project.chagok.backend.scraper.batch.domain.entitiy;

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
    private SiteType siteType;

    private Long visitIdx;

    public SiteVisit(SiteType siteType, Long visitIdx) {
        this.siteType = siteType;
        this.visitIdx = visitIdx;
    }

    public void updateVisitIdx(Long visitIdx) {
        this.visitIdx = visitIdx;
    }
}
