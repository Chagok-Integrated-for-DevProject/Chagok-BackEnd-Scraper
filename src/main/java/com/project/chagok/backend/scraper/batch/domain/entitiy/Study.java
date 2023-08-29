package com.project.chagok.backend.scraper.batch.domain.entitiy;

import com.project.chagok.backend.scraper.constants.SiteType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// Study 게시글 및 Study 게시글 기술 스택 엔티티
@Entity
@Getter
@Setter
@ToString
@Table(name = "study")
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String nickname;

    private LocalDateTime createdTime;

    private int viewCount;

    private String sourceUrl;

    private String content;
    private int hotCount;

    private int scrapCount;

    @Enumerated(EnumType.STRING)
    private SiteType siteType;


    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    @CollectionTable(name = "study_tech_stacks", joinColumns = @JoinColumn(name = "study_id"))
    @Column(name = "tech_stack", nullable = false)
    private List<String> techStacks = new ArrayList<>();

}
