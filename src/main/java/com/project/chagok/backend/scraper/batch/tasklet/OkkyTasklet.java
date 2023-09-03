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
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;

@Component
public class OkkyTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)  {

        Long collectedIdx = (Long) BatchContextUtil.getDataInContext(chunkContext, CollectedIdxKey.OKKY.getKey());

        ObjectMapper objectMapper = new ObjectMapper();
        Document parser;

        ArrayList<String> willParseUrls = new ArrayList<>();

        Long currentIdx = -1L; // 현재 수집하는, 첫번째로 접근한 글에 대한 인덱스

        for (int page = 1; ; page++) {
            String nextUrl = "https://okky.kr/community/gathering?page=" + page;

            try {
                parser = Jsoup
                        .connect(nextUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String listItemsString = parser.selectFirst("#__NEXT_DATA__").data();

            JsonNode listItemsJson = null;
            try {
                listItemsJson = objectMapper.readTree(listItemsString);

                // 게시글 리스트 json 파싱
                Iterator<JsonNode> boardsJsonIter = listItemsJson.get("props").get("pageProps").get("result").get("content").elements();

                while (boardsJsonIter.hasNext()) {
                    JsonNode boardJson = boardsJsonIter.next();

                    // 작성일 파싱
                    LocalDateTime createdDate = convertFromDateString(boardJson.get("dateCreated").asText());
                    // board id 파싱
                    Long boardId = boardJson.get("id").asLong();
                    // url 구성
                    String boardUrl = "https://okky.kr/articles/" + boardId;

                    if (currentIdx == -1L)
                        currentIdx = boardId;

                    if (!validateDate(createdDate) || isVisited(collectedIdx, boardId)) {
                        // job execution context에 파싱할 url list 저장
                        BatchContextUtil.saveDataInContext(chunkContext, ParsingUrlKey.OKKY.getKey(), willParseUrls);
                        // job execution context에 수집했던 첫번째 게시글에 대한 수집 index저장
                        BatchContextUtil.saveDataInContext(chunkContext, CollectedIdxKey.OKKY.getKey(), currentIdx);

                        return RepeatStatus.FINISHED;
                    }
                    willParseUrls.add(boardUrl);
                }

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            try {
                sleep(TimeDelay.MEDIUM);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public LocalDateTime convertFromDateString(String parsingDate) {
        return LocalDateTime.parse(parsingDate);
    }

    // 한달 전 게시글인지 검사
    boolean validateDate(LocalDateTime createdDate) {

        LocalDateTime beforeOneMonth = LocalDateTime.now().minusMonths(1);

        return createdDate.isAfter(beforeOneMonth);
    }
    // 인덱스기반 수집 유무
    boolean isVisited(Long visitIdx, Long boardId) {
        return visitIdx >= boardId;
    }
}
