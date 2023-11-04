package com.project.chagok.backend.scraper.batch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LetsplOverviewDto {

    @JsonProperty("project_no")
    private String projectNo;

    public LetsplOverviewDto(String projectNo) {
        this.projectNo = projectNo;
    }
}
