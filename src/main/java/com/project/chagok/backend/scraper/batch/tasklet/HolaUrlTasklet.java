package com.project.chagok.backend.scraper.batch.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.constants.CollectedIdxKey;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;

@Component
public class HolaUrlTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        // 수집했던 방문 인덱스 조회
        Long collectedIdx = (Long) BatchContextUtil.getDataInContext(chunkContext, CollectedIdxKey.HOLA.getKey());

        ObjectMapper objectMapper = new ObjectMapper();
        Document parser;

        ArrayList<String> willParseUrls = new ArrayList<>();

        Long currentIdx = -1L; // 현재 수집하는, 첫번째로 접근한 글에 대한 인덱스

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

                    // board id 파싱
                    String boardId = boardJson.get("_id").asText();
                    // 수집할 url 구성
                    String boardUrl = "https://holaworld.io/study/" + boardId;

                    if (currentIdx == -1L) // 최초 수집시, index 갱신. 글 작성일을 기준으로 index판단
                        currentIdx = timeToSeconds(createdDate);

                    // 한달 이후 게시글이거나, 이미 방문한 글이라면 종료(순차적 접근)
                    if (!validateDate(createdDate) || isVisited(collectedIdx, createdDate)) {
                        // job execution context에 파싱할 URL 저장
                        BatchContextUtil.saveDataInContext(chunkContext, ParsingUrlKey.HOLA.getKey(), willParseUrls);
                        // job execution context에 수집했던 첫번째 게시글에 대한 수집 index저장
                        BatchContextUtil.saveDataInContext(chunkContext, CollectedIdxKey.HOLA.getKey(), currentIdx);

                        return RepeatStatus.FINISHED;
                    }

                    // 마감일 검사
                    if (!validateDeadLine(deadLineDate))
                        continue;

                    willParseUrls.add(boardUrl);
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

    // 초 단위 시간 변경
    private Long timeToSeconds(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    // 방문 유무
    private boolean isVisited(Long colletedIdx, LocalDateTime boardDateTime) {
        return colletedIdx >= timeToSeconds(boardDateTime);
    }

}
