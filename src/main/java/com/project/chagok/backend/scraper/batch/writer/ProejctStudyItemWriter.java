package com.project.chagok.backend.scraper.batch.writer;

import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProejctStudyItemWriter implements ItemWriter<StudyProjectDto> {

    @Override
    public void write(Chunk<? extends StudyProjectDto> chunk) throws Exception {

        chunk.forEach(data -> log.info("data in -> " + data.toString()));
    }
}
