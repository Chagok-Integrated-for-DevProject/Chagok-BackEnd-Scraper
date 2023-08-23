package com.project.chagok.backend.scraper.batch.writer;

import com.project.chagok.backend.scraper.dto.ContestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContestItemWriter implements ItemWriter<ContestDto> {

    @Override
    public void write(Chunk<? extends ContestDto> chunk) throws Exception {
        chunk.forEach(data -> log.info("data in -> " + data.toString()));
    }
}