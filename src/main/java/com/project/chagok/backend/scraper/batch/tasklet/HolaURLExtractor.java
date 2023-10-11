package com.project.chagok.backend.scraper.batch.tasklet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.sitevisit.HolaVisitor;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Thread.sleep;

@Component
public class HolaURLExtractor extends URLExtractorBase{

    public HolaURLExtractor(HolaVisitor visitor) {
        super(visitor);
    }

    // apiUrl 주소
    final String apiUrl = "https://api.holaworld.io/api/posts/pagination?sort=-createdAt&position=ALL&type=0&isClosed=false&page=";

    @Override
    List<String> extractURL(JobSiteType jobSiteType) {

        ObjectMapper objectMapper = new ObjectMapper();
        Document parser;

        ArrayList<String> willParseUrls = new ArrayList<>();

        for (int page = 1; ; page++) {

            // board api server 주소
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

                    // 한달 이후 게시글이거나, 이미 방문한 글이라면 종료(순차적 접근)
                    if (!validateDate(createdDate) || isVisit(createdDate)) {
                        return willParseUrls;
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

}
