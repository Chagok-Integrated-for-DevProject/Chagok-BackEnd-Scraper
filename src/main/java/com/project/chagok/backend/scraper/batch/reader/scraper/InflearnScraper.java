package com.project.chagok.backend.scraper.batch.reader.scraper;

import com.project.chagok.backend.scraper.batch.reader.ScrapItemReader;
import com.project.chagok.backend.scraper.batch.reader.boardextractor.ProjectStudyBoardExtractor;
import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class InflearnScraper extends ScrapItemReader<StudyProjectDto> implements ProjectStudyBoardExtractor<Document> {

    @Override
    public StudyProjectDto getBoard(String boardUrl) throws IOException {

        Document parser = Jsoup
                .connect(boardUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .get();

        String content = getContent(parser);
        String noTagContent = Jsoup.parse(content).text();

        StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                .siteType(SiteType.INFLEARN)
                .title(getTitle(parser))
                .content(getContent(parser))
                .createdDate(getCreatedTime(parser))
                .sourceUrl(boardUrl)
                .categoryType(getCategoryType(parser))
                .techList(getTechStacks(parser))
                .noTagContent(noTagContent)
                .build();

        return studyProjectDto;
    }

    @Override
    public String getTitle(Document parser) {
        // 제목 파싱
        final String titleSelector = ".header__title h1";
        String title = parser.selectFirst(titleSelector).text();

        return title;
    }

    @Override
    public LocalDateTime getCreatedTime(Document parser) {
        // 작성일 파싱
        final String createdDateSelector = ".sub-title__value";
        LocalDateTime createdDate = extractDateFromDateString(parser.selectFirst(createdDateSelector).text());

        return createdDate;
    }

    @Override
    public List<String> getTechStacks(Document parser) {
        // 기술스택 파싱
        final String techStacksSelector = ".ac-tag__name";
        List<String> techStacks = new ArrayList<>();
        parser.select(techStacksSelector).forEach(techStacksElement -> techStacks.add(techStacksElement.text()));

        return techStacks;
    }

    @Override
    public SiteType getSiteType(Document parser) {
        return null;
    }

    @Override
    public CategoryType getCategoryType(Document parser) {
        return extractCategoryFromUrl(parser.baseUri());
    }

    @Override
    public String getContent(Document parser) {
        // 본문 파싱
        final String contentSelector = ".content__body.markdown-body";
        String content = parser.selectFirst(contentSelector).toString();

        return content;
    }

    private LocalDateTime extractDateFromDateString(String parsingDate) {

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(parsingDate, formatter2);

        return localDateTime;
    }

    private CategoryType extractCategoryFromUrl(String parsingUrl)  {
        try {
            String relativePath = new URL(parsingUrl).getPath();
            int toIdx = relativePath.indexOf("/", 1);

            String type = relativePath.substring(1, toIdx);
            if (type.equals("projects"))
                return CategoryType.PROJECT;
            else if (type.equals("studies"))
                return CategoryType.STUDY;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
