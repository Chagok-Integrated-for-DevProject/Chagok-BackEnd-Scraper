package com.project.chagok.backend.scraper.batch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetsplSearchDto {

    private String location = "KR00";

    private String industry = "00";

    private String recruting = "0000";

    @JsonProperty("recruting_only")
    private boolean recrutingOnly = true;

    private String type = "01";

    private String keyword = "";

    private int offset;

    public LetsplSearchDto(int offset) {
        this.offset = offset;
    }
}
