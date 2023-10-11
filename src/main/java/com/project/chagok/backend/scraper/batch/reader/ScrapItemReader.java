package com.project.chagok.backend.scraper.batch.reader;

import com.project.chagok.backend.scraper.batch.util.BatchContextUtil;
import com.project.chagok.backend.scraper.batch.util.BatchUtil;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

public abstract class ScrapItemReader<T> implements ItemReader<T>, StepExecutionListener {

    private int idx;
    private List<String> boardUrls = null;

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (hasNext()) {
            String boardUrl = next();

            sleep(TimeDelay.MEDIUM);

            return getBoardDto(boardUrl);
        }


        return null;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        StepExecutionListener.super.beforeStep(stepExecution);

        // index 초기화
        idx = 0;
        // 파싱할 url 초기화
        boardUrls = (List<String>) BatchContextUtil.getDataInContext(stepExecution, BatchUtil.SITE_URLS);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }

    private boolean hasNext() {
        return idx < boardUrls.size();
    }

    private String next() {
        return boardUrls.get(idx++);
    }

    public abstract T getBoardDto(String boardUrl) throws IOException;
}
