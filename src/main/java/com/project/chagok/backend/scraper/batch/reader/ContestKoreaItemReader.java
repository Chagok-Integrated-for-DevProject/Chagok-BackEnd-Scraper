package com.project.chagok.backend.scraper.batch.reader;

import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import com.project.chagok.backend.scraper.dto.ContestDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Component
@Slf4j
public class ContestKoreaItemReader implements ItemReader<ContestDto>, StepExecutionListener {

    private ExecutionContext exc;
    private int idx;

    @Override
    public ContestDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        /*
        추출할 데이터
        1. 제목
        2. 개시일
        3. 종료일
        4. 주최기관
        5. 본문
        6. 포스터 URL
         */

        try {
            sleep(TimeDelay.SHORT);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Document parser;
        List<String> boardUrls = (List<String>) exc.get(ParsingUrlKey.CONTEST_KOREA.getKey());

        if (idx < boardUrls.size()) {

            String boardUrl = boardUrls.get(idx++);

            try {
                parser = Jsoup
                        .connect(boardUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            // 제목 파싱
            final String TitleSelector = ".view_top_area.clfx h1";
            String title = parser.select(TitleSelector).text();

            // 개시일 파싱 및 접수 종료일 파싱
            final String receptionDateSelector = "div.clfx  tr:nth-child(4) > td";
            String receptionDateData = parser.select(receptionDateSelector).text();
            // date format 시작일, 종료일 분리 및 LocalDate formatting
            String[] receptionDates = receptionDateData.split(" ~ ");

            LocalDate startReceptionDate = extractDate(receptionDates[0]);
            LocalDate endReceptionDate = extractDate(receptionDates[1]);

            // 주최기관 파싱
            final String hostSelector = "div.txt_area > table > tbody > tr:nth-child(1) > td";
            String host = parser.select(hostSelector).text();

            // 포스터 이미지 파싱
            final String imgSelector = ".img_area:first-child img";
            String imgUrl = "https://contestkorea.com" + parser.select(imgSelector).attr("src");

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

            ContestDto contestDto = ContestDto.builder()
                    .url(boardUrl)
                    .startDate(startReceptionDate)
                    .endDate(endReceptionDate)
                    .host(host)
                    .imgUrl(imgUrl)
                    .title(title)
                    .content(mainContents)
                    .build();

            return contestDto;
        }

        return null;
    }

    private LocalDate extractDate(String parsingDate) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        return LocalDate.parse(parsingDate, formatters);
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
