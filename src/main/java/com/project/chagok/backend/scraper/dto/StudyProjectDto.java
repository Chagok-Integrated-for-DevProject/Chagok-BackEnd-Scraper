package com.project.chagok.backend.scraper.dto;

import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class StudyProjectDto {

    private SiteType siteType;
    private String title;
    private LocalDateTime createdDate;
    private String sourceUrl;
    private CategoryType categoryType;
    private String content;
    private List<String> techList;
    private String noTagContent;
}
