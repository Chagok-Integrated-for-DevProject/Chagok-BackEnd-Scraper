package com.project.chagok.backend.scraper.batch.processor;


import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import com.project.chagok.backend.scraper.service.TokenizeService;
import com.project.chagok.backend.scraper.util.TechsToHashConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectStudyItemProcessor implements ItemProcessor<StudyProjectDto, StudyProjectDto> {

    private final TokenizeService tokenizeService;

    // 기술스택(=skills) txt 파일 읽어서, 해당 기술에 대한 hashmap 불러옴
    HashMap<String, String> skillsMap = TechsToHashConverter.getHashTechs();

    @Override
    public StudyProjectDto process(StudyProjectDto item) {

        extractTechs(item);

        return item;
    }

    public void extractTechs(StudyProjectDto item) {
        HashSet<String> skills = new HashSet<>();
        // 본문 토큰화
        List<String> wordTokens = tokenizeService.tokenizeKorAndEng(item.getNoTagContent());

        List<String> itemTechList = item.getTechList();
        if (!itemTechList.isEmpty()) { // 파싱해온 데이터에 기술스택 데이터가 있을 경우
            // 소문자 변환
            itemTechList = itemTechList.stream().map(String::toLowerCase).collect(Collectors.toList());
            // 기존 데이터의 기술스택 리스트 추가(파싱해온..)
            wordTokens.addAll(itemTechList);
        }

        for (String token : wordTokens) {
            String skill = skillsMap.get(token);
            if (skill != null) { // 해당 skills hashset에 키워드가 존재한다면, 추가.
                skills.add(skill);
            }
        }
        item.setTechList(new ArrayList<>(skills));
    }
}
