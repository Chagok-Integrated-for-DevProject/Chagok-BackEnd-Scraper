package com.project.chagok.backend.scraper.batch.reader.scraper;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.reader.ScrapItemReader;
import com.project.chagok.backend.scraper.batch.reader.boardextractor.ProjectStudyBoardExtractor;
import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HolaScraper extends ScrapItemReader<StudyProjectDto> implements ProjectStudyBoardExtractor<JsonNode> {

    @Override
    public StudyProjectDto getBoard(String boardUrl) throws Exception {

        // boardId 값으로 api 주소에 대입해서 return
        String jsonUrl = extractBoardJsonFromUrl(boardUrl);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest boardJsonRequest = HttpRequest.newBuilder()
                .setHeader("Accept", "application/json")
                .setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                .uri(URI.create(jsonUrl))
                .GET().build();

        String boardJsonStr = client.send(boardJsonRequest, HttpResponse.BodyHandlers.ofString()).body();

        ObjectMapper objectMapper = new ObjectMapper().configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        JsonNode boardJson = objectMapper.readTree(boardJsonStr);

        String content = getContent(boardJson);
        String noTagContent = Jsoup.parse(content).text();

        StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                .siteType(SiteType.HOLA)
                .title(getTitle(boardJson))
                .content(getContent(boardJson))
                .createdDate(getCreatedTime(boardJson))
                .sourceUrl(boardUrl)
                .categoryType(getCategoryType(boardJson))
                .techList(getTechStacks(boardJson))
                .noTagContent(noTagContent)
                .build();

        return studyProjectDto;
    }

    private String extractBoardJsonFromUrl(String parsingUrl) {
        final String BaseJsonUrl = "https://api.holaworld.io/api/posts/";

        Pattern regex = Pattern.compile("study/(.+)");
        Matcher matcher = regex.matcher(parsingUrl);

        if (matcher.find()) {
            String extractedBoardId = matcher.group(1);
            return BaseJsonUrl+extractedBoardId;
        }

        return null;
    }

    @Override
    public String getTitle(JsonNode parser) {
        String title = parser.get("title").asText();
        return title;
    }

    @Override
    public LocalDateTime getCreatedTime(JsonNode parser) {

        String parsingDate = parser.get("createdAt").asText();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parsingDate, formatter);

        return zonedDateTime.toLocalDateTime();
    }

    @Override
    public List<String> getTechStacks(JsonNode parser) {
        List<String> techStacks = new ArrayList<>();
        parser.get("language").elements().forEachRemaining(techstack -> techStacks.add(techstack.asText()));

        return techStacks;
    }

    @Override
    public SiteType getSiteType(JsonNode parser) {
        return null;
    }

    @Override
    public CategoryType getCategoryType(JsonNode parser) {
        String parsingCategoryType = parser.get("type").asText();

        if (parsingCategoryType.equals("1"))
            return CategoryType.PROJECT;
        else if (parsingCategoryType.equals("2"))
            return CategoryType.STUDY;

        return null;
    }

    @Override
    public String getContent(JsonNode parser) {
        // 태그를 포함한 본문 파싱, escape 슬래쉬 제거 및 본문에 \t가 나타나서 제거
        String content = parser.get("content").toString().replace("\\\"", "\"").replace("\\t", "");

        return content;

    }
}
