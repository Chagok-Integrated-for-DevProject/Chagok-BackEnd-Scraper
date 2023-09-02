package com.project.chagok.backend.scraper.batch.reader;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Component
@Slf4j
public class OkkyItemReader implements ItemReader<StudyProjectDto>, StepExecutionListener {

    private ExecutionContext exc;
    private int idx = 0;
    private final String baseUrl = "https://okky.kr/community/gathering";

    @Override
    public StudyProjectDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

                /*
            데이터 목록
            1. 제목
            2. 작성자
            3. 작성일
            4. 기술스택
            5. 출처
            6. 타입(프로젝트 or 스터디)
            7. 본문
         */

        List<String> boardUrls = (List<String>) exc.get(ParsingUrlKey.OKKY.getKey());

        ObjectMapper objectMapper = new ObjectMapper().configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

        Document parser;

        JsonNode listItemsJson = null;
        String apiServerId = null;

        try {
            parser = Jsoup
                    .connect(baseUrl)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .get();

            String listItemsString = parser.selectFirst("#__NEXT_DATA__").data();

            listItemsJson = objectMapper.readTree(listItemsString);
            apiServerId = listItemsJson.get("buildId").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            sleep(TimeDelay.MEDIUM);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        if (idx < boardUrls.size()) {

            String boardUrl = boardUrls.get(idx++);

            String jsonUrl = extractBoardJsonFromUrl(boardUrl, apiServerId);

            try {
                parser = Jsoup
                        .connect(jsonUrl)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // board json 파싱
            String boardJsonString = parser.select("body").first().html().replaceAll("\"\\\\&quot;|\\\\&quot;\"", "\\\\\"");

            JsonNode boardJson = objectMapper.readTree(boardJsonString);

            String title = boardJson.get("pageProps").get("result").get("title").asText();
            String nickname = boardJson.get("pageProps").get("result").get("displayAuthor").get("nickname").asText();
            String content = boardJson.get("pageProps").get("result").get("content").get("text").toString().replace("\\\"", "\"");
            LocalDateTime createdTime = LocalDateTime.parse(boardJson.get("pageProps").get("result").get("dateCreated").asText());
            List<String> techStacksList = new ArrayList<>();
            boardJson.get("pageProps").get("result").get("tags").elements().forEachRemaining(techElement -> techStacksList.add(techElement.get("name").asText()));
            String sourceUrl = boardUrl;
            CategoryType category = CategoryType.STUDY;

            StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                    .siteType(SiteType.OKKY)
                    .title(title)
                    .content(content)
                    .nickname(nickname)
                    .createdDate(createdTime)
                    .sourceUrl(sourceUrl)
                    .categoryType(category)
                    .techList(techStacksList)
                    .build();

            return studyProjectDto;
        }

        return null;
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
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);

        // Execution Context 초기화
        exc = BatchContextUtil.getExecutionContextOfJob(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
