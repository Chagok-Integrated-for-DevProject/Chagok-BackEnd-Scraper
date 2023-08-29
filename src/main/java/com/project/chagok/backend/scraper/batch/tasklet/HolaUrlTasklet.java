package com.project.chagok.backend.scraper.batch.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.utils.BatchUtils;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static java.lang.Thread.sleep;

@Component
public class HolaUrlTasklet implements Tasklet {

    private HashSet<String> visitedUrls = new HashSet<>();
    private ExecutionContext exc;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        // 방문 인덱스 조회
        Long visitIdx = (Long) BatchUtils.getDataInContext(chunkContext, BatchUtils.HOLA_VISIT_IDX_KEY);

        ObjectMapper objectMapper = new ObjectMapper();
        Document parser;

        ArrayList<String> willParseUrls = new ArrayList<>();

        for (int page = 1; ; page++) {

            // apiUrl 주소에 page 추가
            // board api server
            String apiUrl = "https://api.holaworld.io/api/posts/pagination?sort=-createdAt&position=ALL&type=0&isClosed=false&page=";
            String nextUrl = apiUrl + page;

            try {
                parser = Jsoup
                        .connect(nextUrl)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 게시글 리스트 JSON 파싱 - 마감일이 지나지 않은 것만
            String listItemsString = parser.select("body").text();


            try {
                JsonNode listItemsJson = objectMapper.readTree(listItemsString);

                // 게시글 리스트 json 파싱
                Iterator<JsonNode> boardsJsonIter = listItemsJson.get("posts").elements();

                while (boardsJsonIter.hasNext()) {
                    JsonNode boardJson = boardsJsonIter.next();

                    // 마감일 파싱
                    LocalDateTime deadLineDate = convertFromDateString(boardJson.get("startDate").asText());
                    LocalDateTime createdDate = convertFromDateString(boardJson.get("createdAt").asText());

                    String boardId = boardJson.get("_id").asText();
                    String boardUrl = "https://holaworld.io/study/" + boardId;

                    // 한달 전 게시글만 수집
                    if (!validateDate(createdDate) || isVisited(visitIdx, createdDate)) {
                        // job execution 컨텍스트에 파싱할 URL 저장
                        BatchUtils.saveDataInContext(chunkContext, BatchUtils.HOLA_PARSING_URL_KEY, willParseUrls);
                        // job execution 컨텍스트에 visit 인덱스 저장
                        BatchUtils.saveDataInContext(chunkContext, BatchUtils.HOLA_VISIT_IDX_KEY, visitIdx);

                        return RepeatStatus.FINISHED;
                    }

                    // 마감일 검사
                    if (!validateDeadLine(deadLineDate))
                        continue;

                    willParseUrls.add(boardUrl);
                    visitIdx = createdDate.toEpochSecond(ZoneOffset.UTC);
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            try {
                // 페이지네이션 딜레이
                sleep(TimeDelay.MEDIUM);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // date 포맷 convert
    private LocalDateTime convertFromDateString(String parsingDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parsingDate, formatter);

        return zonedDateTime.toLocalDateTime();
    }

    // date 검증
    private boolean validateDate(LocalDateTime createdDate) {

        LocalDateTime beforeOneMonth = LocalDateTime.now().minusMonths(1);

        return createdDate.isAfter(beforeOneMonth);
    }

    // 마감일 검증
    private boolean validateDeadLine(LocalDateTime deadLineDate) {
        // 마감일이 현재 날짜 이후라면, true
        return deadLineDate.isAfter(LocalDateTime.now());
    }

    // 방문 유무
    private boolean isVisited(Long visitIdx, LocalDateTime boardDateTime) {
        return visitIdx >= boardDateTime.toEpochSecond(ZoneOffset.UTC);
    }

}
