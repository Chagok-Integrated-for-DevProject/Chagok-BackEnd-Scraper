package com.project.chagok.backend.scraper.batch.processor;


import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ProjectStudyItemProcessor implements ItemProcessor<StudyProjectDto, StudyProjectDto> {

    @Override
    public StudyProjectDto process(StudyProjectDto item) {
        if (item.getSiteType() == SiteType.HOLA) { // HOLA 사이트, 본문에 \t가 나타나서 제거
            if (item.getContent().contains("\\t"))
                item.setContent(item.getContent().replace("\\t", ""));
        }

        return item;
    }
}
