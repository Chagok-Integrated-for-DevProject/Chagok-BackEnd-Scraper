package com.project.chagok.backend.scraper.batch.tasklet;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.constants.VisitIdxKey;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;


@Component
@Slf4j
public class InflearnTasklet implements Tasklet {

    private final String projectUrl = "https://www.inflearn.com/community/projects?status=unrecruited";
    private final String studyUrl = "https://www.inflearn.com/community/studies?status=unrecruited";
    private final String baseInflearnUrl = "https://www.inflearn.com";


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        JobSiteType jobSiteType = (JobSiteType) BatchContextUtil.getDataInRunParam(chunkContext, BatchUtil.SITE_TYPE_KEY);

        Long visitIdx = null;
        String baseUrl = null;
        if (jobSiteType == JobSiteType.INFLEARN_PROJECT) {
            baseUrl = projectUrl;
            visitIdx = (Long) BatchContextUtil.getDataInContext(chunkContext, VisitIdxKey.INFLEARN_PROJECT.getKey());
        }
        else if (jobSiteType == JobSiteType.INFLEARN_STUDY) {
            baseUrl = studyUrl;
            visitIdx = (Long) BatchContextUtil.getDataInContext(chunkContext, VisitIdxKey.INFLEARN_STUDY.getKey());
        }

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
                String url = baseInflearnUrl + listItemElement.selectFirst("a").attr("href");
                Long boardId = extractBoardId(url);

                // 한 달 전인지 검증 or 이미 방문한 사이트인지 검증
                if (!validateDate(createdDate) || isVisited(visitIdx, boardId)) {
                    // 컨텍스트에 파싱할 url 저장
                    BatchContextUtil.saveDataInContext(chunkContext, ParsingUrlKey.INFLEARN.getKey(), willParseUrls);
                    BatchContextUtil.saveDataInContext(chunkContext, (jobSiteType == JobSiteType.INFLEARN_STUDY) ? VisitIdxKey.INFLEARN_STUDY.getKey() : VisitIdxKey.INFLEARN_PROJECT.getKey(), boardId);

                    return RepeatStatus.FINISHED;
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

    boolean isVisited(Long visitIdx, Long boardId) {
        return visitIdx >= boardId;
    }

    private boolean isRecruiting(String recruitingString) {
        return recruitingString.equals("모집중");
    }

    private boolean validateDate(String parsingDate) {

        final String validDateSuffix = "달 전";

        return !parsingDate.endsWith(validDateSuffix);
    }

    private Long extractBoardId(String url) {
        String pattern = "/(?:studies|projects)/(\\d+)";

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(url);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

}
