package com.project.chagok.backend.scraper.batch.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.sitevisit.OkkyVisitor;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

@Component
public class OkkyURLExtractor extends URLExtractorBase{

    public OkkyURLExtractor(OkkyVisitor visitor) {
        super(visitor);
    }

    @Override
    List<String> extractURL(JobSiteType jobSiteType) {

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

                    if (!validateDate(createdDate) || visitor.isVisit(createdDate)) {
                        return willParseUrls;
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

}
