package com.project.chagok.backend.scraper.batch.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.constants.VisitIdxKey;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
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
import java.util.HashSet;
import java.util.Iterator;

import static java.lang.Thread.sleep;

@Component
public class OkkyTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)  {

        Long visitIdx = (Long) BatchContextUtil.getDataInContext(chunkContext, VisitIdxKey.OKKY.getKey());

        ObjectMapper objectMapper = new ObjectMapper();
        Document parser;

        ArrayList<String> willParseUrls = new ArrayList<>();

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

                Iterator<JsonNode> boardsJsonIter = listItemsJson.get("props").get("pageProps").get("result").get("content").elements();

                while (boardsJsonIter.hasNext()) {
                    JsonNode boardJson = boardsJsonIter.next();

                    LocalDateTime createdDate = convertFromDateString(boardJson.get("dateCreated").asText());

                    Long boardId = boardJson.get("id").asLong();
                    String boardUrl = "https://okky.kr/articles/" + boardId;

                    if (!validateDate(createdDate) || isVisited(visitIdx, boardId)) {
                        BatchContextUtil.saveDataInContext(chunkContext, ParsingUrlKey.OKKY.getKey(), willParseUrls);
                        BatchContextUtil.saveDataInContext(chunkContext, VisitIdxKey.OKKY.getKey(), boardId);

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

    boolean validateDate(LocalDateTime createdDate) {

        LocalDateTime beforeOneMonth = LocalDateTime.now().minusMonths(1);

        return createdDate.isAfter(beforeOneMonth);
    }

    boolean isVisited(Long visitIdx, Long boardId) {
        return visitIdx >= boardId;
    }
}
