package com.project.chagok.backend.scraper.batch.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Component
public class HolaItemReader implements ItemReader<StudyProjectDto>, StepExecutionListener {

    private ExecutionContext exc;
    private int idx;

    @Override
    public StudyProjectDto read() {

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

        List<String> boardUrls = (List<String>) exc.get(ParsingUrlKey.HOLA.getKey());

        ObjectMapper objectMapper = new ObjectMapper().configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        Document parser;

        // 게시판 데이터 추출 딜레이
        try {
            sleep(TimeDelay.MEDIUM);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (idx < boardUrls.size()) {

            String boardUrl = boardUrls.get(idx++);

            // boardId 값으로 api 주소에 대입해서 return
            String jsonUrl = extractBoardJsonFromUrl(boardUrl);

            try {
                parser = Jsoup
                        .connect(jsonUrl)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // board json 파싱 & jsoup에서 특정 escape 문자열을 encoding 한 것에 \" 로 decoding
            String boardJsonString = parser.select("body").first().html().replaceAll("\"\\\\&quot;|\\\\&quot;\"", "\\\\\"");

            try {
                JsonNode boardJson = objectMapper.readTree(boardJsonString);

                String title = boardJson.get("title").asText();
                String content = boardJson.get("content").toString().replace("\\\"", "\"");
                String nickname = boardJson.get("author").get("nickName").asText();
                LocalDateTime createdTime = convertFromDateString(boardJson.get("createdAt").asText());
                List<String> techStacksList = new ArrayList<>();
                boardJson.get("language").elements().forEachRemaining(techstack -> techStacksList.add(techstack.asText()));
                CategoryType category = extractCategoryFromJSon(boardJson.get("type").asText());


                StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                        .siteType(SiteType.HOLA)
                        .title(title)
                        .content(content)
                        .nickname(nickname)
                        .createdDate(createdTime)
                        .sourceUrl(boardUrl)
                        .categoryType(category)
                        .techList(techStacksList)
                        .build();

                return studyProjectDto;

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return null;
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

    private CategoryType extractCategoryFromJSon(String parsingCategory) {
        if (parsingCategory.equals("1"))
            return CategoryType.PROJECT;
        else if (parsingCategory.equals("2"))
            return CategoryType.STUDY;

        return null;
    }

    private LocalDateTime convertFromDateString(String parsingDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parsingDate, formatter);

        return zonedDateTime.toLocalDateTime();
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        idx = 0;

        StepExecutionListener.super.beforeStep(stepExecution);

        // Execution Context 초기화
        exc = BatchContextUtil.getExecutionContextOfJob(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }


}
