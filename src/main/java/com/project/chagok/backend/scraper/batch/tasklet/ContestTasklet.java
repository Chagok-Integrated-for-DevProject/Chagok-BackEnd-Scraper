package com.project.chagok.backend.scraper.batch.tasklet;

import com.project.chagok.backend.scraper.batch.utils.BatchUtils;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Thread.sleep;


@Component
@RequiredArgsConstructor
public class ContestTasklet implements Tasklet {

    private final WebDriver chromeDriver;
    private HashSet<Long> visitedPages = new HashSet<>();
    private final String hackaThonUrl = "https://contestkorea.com/sub/list.php?int_gbn=1&Txt_bcode=030510001";


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        chromeDriver.get(hackaThonUrl);

        // 접수중 버튼 셀렉터
        final String RegistrationBtnSelector = "#frm > div > div.clfx.mb_20 > div.f-r > ul > li:nth-child(4) > button";
        // 접수중인 페이지로 이동
        chromeDriver.findElement(By.cssSelector(RegistrationBtnSelector)).click();

        ArrayList<String> willParseUrls = new ArrayList<>();

        while(true) {
            // 리스트 a태그 셀렉터
            final String ListLinkSelector = ".list_style_2 .title a";
            List<WebElement> elementList = chromeDriver.findElements(By.cssSelector(ListLinkSelector));

            // 페이지에 대한 링크를 전부 list에 담음
            for (WebElement element : elementList) {

                // list item url 파싱
                String url = element.getAttribute("href");

                // 페이지 Id 추출하기 위한 delimiter
                final String pageIdDel = "str_no=";
                Long pageId = Long.valueOf(url.substring(url.lastIndexOf(pageIdDel) + pageIdDel.length()));

                // 페이지가 존재하지 않는다면..
                if (!visitedPages.contains(pageId)) {
                    willParseUrls.add(url);
                    visitedPages.add(pageId);
                }
            }

            // 다음 page 버튼 셀렉터
            final String PaginationNextBtnSelector = ".pagination .mg_right";
            WebElement nextButton = chromeDriver.findElement(By.cssSelector(PaginationNextBtnSelector));

            // 해당 페이지는 다음 버튼 페이지네이션을 js onclick기반으로 동작하기 때문에, onclick유무에 따라 다음 페이지가 있는지 없는지 확인 가능
            if (nextButton.getAttribute("onclick") == null) {
                break;
            }

            try {
                sleep(TimeDelay.SHORT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 다음페이지로 이동
            nextButton.click();
        }

        // execution 컨텍스트에 파싱할 URL 저장
        ExecutionContext exc = BatchUtils.getExecutionContextOfJob(chunkContext);
        exc.put(BatchUtils.CONTEST_PARSING_URL_KEY, willParseUrls);

        return RepeatStatus.FINISHED;
    }
}
