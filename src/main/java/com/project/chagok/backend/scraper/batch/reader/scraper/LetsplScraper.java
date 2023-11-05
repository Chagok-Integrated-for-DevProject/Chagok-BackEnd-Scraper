package com.project.chagok.backend.scraper.batch.reader.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.dto.LetsplOverviewDto;
import com.project.chagok.backend.scraper.batch.reader.ScrapItemReader;
import com.project.chagok.backend.scraper.batch.reader.boardextractor.ProjectStudyBoardExtractor;
import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class LetsplScraper extends ScrapItemReader<StudyProjectDto> implements ProjectStudyBoardExtractor<JsonNode>{

    private static final String boardApiUrl = "https://letspl.me/find_project/overview";
    private static final String boardBaseUrl = "https://letspl.me/project/";
    private ObjectMapper om = new ObjectMapper();

    @Override
    public StudyProjectDto getBoard(String boardUrl) throws Exception {

        String projectNo = boardUrl;

        String reqProejctJsonStr = om.writeValueAsString(new LetsplOverviewDto(projectNo));

        HttpRequest boardJsonRequest = HttpRequest.newBuilder()
                .uri(new URI(boardApiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqProejctJsonStr))
                .setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        String boardJsonStr = client.send(boardJsonRequest, HttpResponse.BodyHandlers.ofString()).body();

        JsonNode boardJson = om.readTree(boardJsonStr).get("project_overview");

        String content = getContent(boardJson);
        String noTagContent = Jsoup.parse(content).text();

        String sourceUrl = boardBaseUrl + projectNo;

        StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                .siteType(SiteType.LETSPL)
                .title(getTitle(boardJson))
                .content(getContent(boardJson))
                .createdDate(getCreatedTime(boardJson))
                .sourceUrl(sourceUrl)
                .categoryType(CategoryType.PROJECT)
                .techList(getTechStacks(boardJson))
                .noTagContent(noTagContent)
                .build();

        return studyProjectDto;
    }

    @Override
    public String getTitle(JsonNode parser) {
        return parser.get("PROJECT_NAME").asText();
    }

    @Override
    public LocalDateTime getCreatedTime(JsonNode parser) {
        return LocalDate.parse(parser.get("PROJECT_CREATE_TIME").asText()).atStartOfDay();
    }

    @Override
    public List<String> getTechStacks(JsonNode parser) {
        return Arrays.asList(parser.get("PROJECT_TECH").asText().split(","));
    }

    @Override
    public SiteType getSiteType(JsonNode parser) {
        return null;
    }

    @Override
    public CategoryType getCategoryType(JsonNode parser) {
        return null;
    }

    @Override
    public String getContent(JsonNode parser) {
        return parser.get("PROJECT_INTRODUCTION").toString().replace("\\\"", "\"");
    }
}
