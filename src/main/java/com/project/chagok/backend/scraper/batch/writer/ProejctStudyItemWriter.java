package com.project.chagok.backend.scraper.batch.writer;

import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import com.project.chagok.backend.scraper.service.ScrapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProejctStudyItemWriter implements ItemWriter<StudyProjectDto> {

    private final ScrapService scrapService;

    @Override
    public void write(Chunk<? extends StudyProjectDto> chunk) throws Exception {

        chunk.forEach(scrapService::saveStudyProject);
    }
}
