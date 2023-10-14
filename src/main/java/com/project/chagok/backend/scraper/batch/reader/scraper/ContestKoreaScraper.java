package com.project.chagok.backend.scraper.batch.reader.scraper;

import com.project.chagok.backend.scraper.batch.reader.ScrapItemReader;
import com.project.chagok.backend.scraper.batch.reader.boardextractor.ContestBoardExtractor;
import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.dto.ContestDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ContestKoreaScraper extends ScrapItemReader<ContestDto> implements ContestBoardExtractor<Document> {

    @Override
    public ContestDto getBoardDto(String boardUrl) throws IOException {

        Document parser = Jsoup
                    .connect(boardUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .get();

        return ContestDto.builder()
                .title(getTitle(parser))
                .url(boardUrl)
                .startDate(getStartReceptionDate(parser))
                .endDate(getEndReceptionDate(parser))
                .host(getHost(parser))
                .content(getContent(parser))
                .imgUrl(getMainImgUrl(parser))
                .siteType(SiteType.CONTEST_KOREA)
                .build();
    }

    @Override
    public String getTitle(Document parser) {
        // 제목 파싱
        final String TitleSelector = ".view_top_area.clfx h1";
        String title = parser.select(TitleSelector).text();

        return title;
    }

    @Override
    public LocalDate getStartReceptionDate(Document parser) {
        // 개시일 파싱 및 접수 종료일 파싱
        final String receptionDateSelector = "div.clfx  tr:nth-child(4) > td";
        String receptionDateData = parser.select(receptionDateSelector).text();
        // date format 시작일, 종료일 분리 및 LocalDate formatting
        String[] receptionDates = receptionDateData.split(" ~ ");

        return extractDate(receptionDates[0]);
    }

    @Override
    public LocalDate getEndReceptionDate(Document parser) {
        // 개시일 파싱 및 접수 종료일 파싱
        final String receptionDateSelector = "div.clfx  tr:nth-child(4) > td";
        String receptionDateData = parser.select(receptionDateSelector).text();
        // date format 시작일, 종료일 분리 및 LocalDate formatting
        String[] receptionDates = receptionDateData.split(" ~ ");

        return extractDate(receptionDates[1]);
    }

    @Override
    public String getHost(Document parser) {
        // 주최기관 파싱
        final String hostSelector = "div.txt_area > table > tbody > tr:nth-child(1) > td";
        String host = parser.select(hostSelector).text();

        return host;
    }

    @Override
    public String getContent(Document parser) {
        // 글 본문
        Elements contentsElements = parser.select(".view_detail_area .txt").first().children();

        String mainContents = contentsElements
                .stream()
                .filter(content -> !(content.hasClass("tip") || content.hasClass("tip_box") || content.hasClass("attachments")
                        || content.hasClass("img_area") || content.hasClass("channel_list"))
                )
                .map(Objects::toString)
                .collect(Collectors.joining(""));

        if (mainContents.contains("<h2>주최·주관사 SNS 채널</h2>"))
            mainContents = mainContents.concat("<p>해당 홈페이지 참조</p>");

        return mainContents;
    }

    @Override
    public String getMainImgUrl(Document parser) {
        // 포스터 이미지 파싱
        final String imgSelector = ".img_area:first-child img";
        String imgUrl = "https://contestkorea.com" + parser.select(imgSelector).attr("src");

        return imgUrl;
    }

    private LocalDate extractDate(String parsingDate) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return LocalDate.parse(parsingDate, formatters);
    }
}
