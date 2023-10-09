package com.project.chagok.backend.scraper.domain.entitiy;

import com.project.chagok.backend.scraper.constants.SiteType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private LocalDateTime createdTime;

    private int viewCount;

    private String sourceUrl;

    private String content;

    private int scrapCount;

    private int hotCount;

    @Enumerated(EnumType.STRING)
    private SiteType siteType;

    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "project_tech_stacks", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech_stacks", nullable = false)
    private List<String> techStacks;
}
