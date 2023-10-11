package com.project.chagok.backend.scraper.dto;

import com.project.chagok.backend.scraper.constants.SiteType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@ToString
public class ContestDto {

    private String url;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String host;
    private String imgUrl;
    private String content;
    private SiteType siteType;

}
