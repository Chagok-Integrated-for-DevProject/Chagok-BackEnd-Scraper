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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OkkyScraper extends ScrapItemReader<StudyProjectDto> implements ProjectStudyBoardExtractor<JsonNode> {

    private final String baseUrl = "https://okky.kr/community/gathering";

    @Override
    public StudyProjectDto getBoard(String boardUrl) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper().configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

        Document parser = Jsoup
                .connect(baseUrl)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .get();

        String listItemsString = parser.selectFirst("#__NEXT_DATA__").data();

        JsonNode listItemsJson = objectMapper.readTree(listItemsString);
        String apiServerId = listItemsJson.get("buildId").asText();

        String jsonUrl = extractBoardJsonFromUrl(boardUrl, apiServerId);
        parser = Jsoup
                .connect(jsonUrl)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .get();

        // board json 파싱
        String boardJsonString = parser.select("body").first().html().replaceAll("\"\\\\&quot;|\\\\&quot;\"", "\\\\\"");

        JsonNode boardJson = objectMapper.readTree(boardJsonString);

        String content = getContent(boardJson);
        String noTagContent = Jsoup.parse(content).text();

        StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                .siteType(SiteType.OKKY)
                .title(getTitle(boardJson))
                .content(content)
                .createdDate(getCreatedTime(boardJson))
                .sourceUrl(boardUrl)
                .categoryType(CategoryType.STUDY)
                .techList(getTechStacks(boardJson))
                .noTagContent(noTagContent)
                .build();

        return studyProjectDto;
    }

    private String extractBoardJsonFromUrl(String parsingUrl, String apiServerId) {
        final String BaseJsonUrl = "https://okky.kr/_next/data/";

        Pattern regex = Pattern.compile("articles/(\\d+)");
        Matcher matcher = regex.matcher(parsingUrl);

        if (matcher.find()) {
            String extractedBoardId = matcher.group(1);

            return BaseJsonUrl + apiServerId + "/articles/" + extractedBoardId + ".json";
        }

        return null;
    }

    @Override
    public String getTitle(JsonNode parser) {
        // 제목 파싱
        String title = parser.get("pageProps").get("result").get("title").asText();

        return title;
    }

    @Override
    public LocalDateTime getCreatedTime(JsonNode parser) {
        // 생성일 파싱
        LocalDateTime createdTime = LocalDateTime.parse(parser.get("pageProps").get("result").get("dateCreated").asText());

        return createdTime;
    }

    @Override
    public List<String> getTechStacks(JsonNode parser) {
        // 기술태그 파싱
        List<String> techStacks = new ArrayList<>();
        parser.get("pageProps").get("result").get("tags").elements().forEachRemaining(techElement -> techStacks.add(techElement.get("name").asText()));

        return techStacks;
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
        // 본문 파싱
        String content = parser.get("pageProps").get("result").get("content").get("text").toString().replace("\\\"", "\"");

        return content;

    }
}
