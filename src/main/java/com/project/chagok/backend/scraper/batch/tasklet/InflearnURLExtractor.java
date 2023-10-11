package com.project.chagok.backend.scraper.batch.tasklet;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.sitevisit.InflearnVisitor;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@Component
public class InflearnURLExtractor extends URLExtractorBase{

    public InflearnURLExtractor(InflearnVisitor visitor) {
        super(visitor);
    }

    // 수집 할 url
    private final String projectUrl = "https://www.inflearn.com/community/projects?status=unrecruited";
    private final String studyUrl = "https://www.inflearn.com/community/studies?status=unrecruited";

    @Override
    List<String> extractURL(JobSiteType jobSiteType) {

        // inflearn project or study에 따른
        String baseUrl = null;
        if (jobSiteType == JobSiteType.INFLEARN_PROJECT)
            baseUrl = projectUrl;
        else if (jobSiteType == JobSiteType.INFLEARN_STUDY)
            baseUrl = studyUrl;

        ArrayList<String> willParseUrls = new ArrayList<>();

        for (int page = 1; ; page++){
            Document parser;

            String nextPageUrl = baseUrl + "&page=" + page;

            try {
                parser = Jsoup
                        .connect(nextPageUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // list items 목록 파싱
            final String listItemsSelector = ".question-container";
            Elements listItemsElements = parser.select(listItemsSelector);

            for (Element listItemElement : listItemsElements) {

                // list item 생성일 파싱
                final String listCreatedDateSelector = ".question__info-detail span:nth-child(3)";
                String createdDate = listItemElement.selectFirst(listCreatedDateSelector).text();

                // list item url 파싱
                String url = listItemElement.selectFirst("a").absUrl("href");

                // 한 달 전인지 검증 or 이미 방문한 사이트인지 검증
                if (!validateDate(createdDate) || isVisit(url)) {
                    return willParseUrls;
                }

                // list item 모집유무 파싱(모집중 or 모집완료)
                final String recruitingSelector = ".badge";
                // 모집유무 검증
                if (!isRecruiting(listItemElement.selectFirst(recruitingSelector).text()))
                    continue;

                willParseUrls.add(url);
            }

            try {
                sleep(TimeDelay.MEDIUM);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private boolean isRecruiting(String recruitingString) {
        return recruitingString.equals("모집중");
    }
    // 한 달전 게시글 검증
    private boolean validateDate(String parsingDate) {

        final String validDateSuffix = "달 전";

        return !parsingDate.endsWith(validDateSuffix);
    }

}
