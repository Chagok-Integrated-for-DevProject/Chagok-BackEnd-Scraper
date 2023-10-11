package com.project.chagok.backend.scraper.batch.reader.boardextractor;


import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.constants.SiteType;

import java.time.LocalDateTime;
import java.util.List;

/*
    프로젝트 스터디 스케마

    1. 제목
    2. 작성일
    3. 기술스택
    4. 출처
    5. 타입(프로젝트 or 스터디)
    6. 본문
*/
public interface ProjectStudyBoardExtractor<T> {
    String getTitle(T parser);
    LocalDateTime getCreatedTime(T parser);
    List<String> getTechStacks(T parser);
    SiteType getSiteType(T parser);
    CategoryType getCategoryType(T parser);
    String getContent(T parser);
}
