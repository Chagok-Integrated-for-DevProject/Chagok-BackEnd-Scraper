package com.project.chagok.backend.scraper.batch.writer;

import com.project.chagok.backend.scraper.dto.ContestDto;
import com.project.chagok.backend.scraper.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContestItemWriter implements ItemWriter<ContestDto> {

    private final ScrapService scrapService;

    @Override
    public void write(Chunk<? extends ContestDto> chunk) throws Exception {
        log.info(chunk.toString());
        chunk.forEach(scrapService::saveContest);
    }
}
