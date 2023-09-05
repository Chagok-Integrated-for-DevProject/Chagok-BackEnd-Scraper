package com.project.chagok.backend.scraper.batch.tasklet;

import com.project.chagok.backend.scraper.batch.constants.CollectedIdxKey;
import com.project.chagok.backend.scraper.batch.constants.ParsingUrlKey;
import com.project.chagok.backend.scraper.batch.factory.WebDriverFactory;
import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


@Component
@RequiredArgsConstructor
public class ContestKoreaTasklet implements Tasklet {


    private final WebDriverFactory webDriverFactory;
    private WebDriver chromeDriver;

    // 공모전 url
    private final String hackaThonUrl = "https://contestkorea.com/sub/list.php?int_gbn=1&Txt_bcode=030510001";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws MalformedURLException {

        chromeDriver = webDriverFactory.getChromeDriver();
        // 수집했던 방문 인덱스 조회
        Long collectedIdx = (Long) BatchContextUtil.getDataInContext(chunkContext, CollectedIdxKey.CONTEST_KOREA.getKey());
        // 이전에 수집했던 인덱스를 기반으로 초기화
        Long currentIdx = collectedIdx;

        ArrayList<String> willParseUrls = new ArrayList<>();

        chromeDriver.get(hackaThonUrl);

        // 접수중 버튼 셀렉터
        final String RegistrationBtnSelector = "#frm > div > div.clfx.mb_20 > div.f-r > ul > li:nth-child(4) > button";
        // 접수중인 페이지로 이동
        chromeDriver.findElement(By.cssSelector(RegistrationBtnSelector)).click();


        // 모든 페이지를 방문
        while(true) {
            // 리스트 a태그 셀렉터
            final String ListLinkSelector = ".list_style_2 .title a";
            List<WebElement> elementList = chromeDriver.findElements(By.cssSelector(ListLinkSelector));

            // 페이지에 대한 링크를 전부 list에 담음
            for (WebElement element : elementList) {

                // list item url 파싱
                String url = element.getAttribute("href");
                // boardId 추출
                Long boardId = extractBoardIdToLong(url);

                // 방문하지 않은 글에 대해서
                if (!isVisited(collectedIdx, boardId)) {
                    willParseUrls.add(url);
                    // currentIdx 갱신(가장 높은 값)
                    currentIdx = Long.max(currentIdx, boardId);
                }
            }

            // 다음 page 버튼 셀렉터
            final String PaginationNextBtnSelector = ".pagination .mg_right";
            WebElement nextButton = chromeDriver.findElement(By.cssSelector(PaginationNextBtnSelector));

            // 해당 페이지는 다음 버튼 페이지네이션을 js onclick기반으로 동작하기 때문에, onclick유무에 따라 다음 페이지가 있는지 없는지에 따른 탈출조건
            if (nextButton.getAttribute("onclick") == null) {
                // job execution 컨텍스트에 파싱할 URL 저장
                BatchContextUtil.saveDataInContext(chunkContext, ParsingUrlKey.CONTEST_KOREA.getKey(), willParseUrls);
                // job execution 컨텍스트에 current idx 저장
                BatchContextUtil.saveDataInContext(chunkContext, CollectedIdxKey.CONTEST_KOREA.getKey(), currentIdx);

                chromeDriver.quit();

                return RepeatStatus.FINISHED;
            }

            try {
                sleep(TimeDelay.SHORT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 다음페이지로 이동
            nextButton.click();
        }
    }

    // url의 board id 추출
    private Long extractBoardIdToLong(String url) {
        final String pageIdDel = "str_no=";
        return Long.parseLong(url.substring(url.lastIndexOf(pageIdDel) + pageIdDel.length()));
    }

    // 방문 유무 검사
    private boolean isVisited(Long collectedIdx, Long boardId) {
        return collectedIdx >= boardId;
    }
}
