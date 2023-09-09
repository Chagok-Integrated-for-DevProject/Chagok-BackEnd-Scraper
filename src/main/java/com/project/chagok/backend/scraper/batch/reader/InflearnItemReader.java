package com.project.chagok.backend.scraper.batch.reader;

import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


@Component
public class InflearnItemReader implements ItemReader<StudyProjectDto>, StepExecutionListener {

    private ExecutionContext exc;
    private int idx;

    @Override
    public StudyProjectDto read() {

         /*
        데이터 목록
        1. 제목
        2. 작성자
        3. 작성일
        4. 기술스택
        5. 출처
        6. 타입(프로젝트 or 스터디)
        7. 본문
         */

        List<String> boardUrls = (List<String>) exc.get(ParsingUrlKey.INFLEARN.getKey());

        try {
            sleep(TimeDelay.MEDIUM);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (idx < boardUrls.size()) {

            String boardUrl = boardUrls.get(idx++);

            Document parser;

            try {
                parser = Jsoup
                        .connect(boardUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 제목 파싱
            final String titleSelector = ".header__title h1";
            String title = parser.selectFirst(titleSelector).text();

            // 닉네임 파싱
            final String nicknameSelector = ".user-name a";
            String nickname = parser.selectFirst(nicknameSelector).text();

            // 작성일 파싱
            final String createdDateSelector = ".sub-title__value";
            LocalDateTime createdDate = extractDateFromDateString(parser.selectFirst(createdDateSelector).text());

            // 기술스택 파싱
            final String techStacksSelector = ".ac-tag__name";
            List<String> techStacks = new ArrayList<>();
            parser.select(techStacksSelector).forEach(techStacksElement -> techStacks.add(techStacksElement.text()));

            // 본문 파싱
            final String contentSelector = ".content__body.markdown-body";
            String content = parser.selectFirst(contentSelector).toString();

            // 태그를 제외한 본문 파싱
            String noTagContent = parser.selectFirst(contentSelector).text();

            // 원글 url
            String sourceUrl = boardUrl;

            //마감일
            CategoryType type = extractCategoryFromUrl(boardUrl);

            StudyProjectDto studyProjectDto = StudyProjectDto.builder()
                    .siteType(SiteType.INFLEARN)
                    .title(title)
                    .content(content)
                    .nickname(nickname)
                    .createdDate(createdDate)
                    .sourceUrl(sourceUrl)
                    .categoryType(type)
                    .techList(techStacks)
                    .noTagContent(noTagContent)
                    .build();

            return studyProjectDto;
        }


        return null;
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


    @Override
    public void beforeStep(StepExecution stepExecution) {
        idx = 0;

        StepExecutionListener.super.beforeStep(stepExecution);

        // Execution Context 초기화
        exc = BatchContextUtil.getExecutionContextOfJob(stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
