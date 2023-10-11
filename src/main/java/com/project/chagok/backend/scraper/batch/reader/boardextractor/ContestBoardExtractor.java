package com.project.chagok.backend.scraper.batch.reader.boardextractor;

import java.time.LocalDate;

/*
    공모전 스케마

    1. 제목
    2. 개시일
    3. 종료일
    4. 주최기관
    5. 본문
    6. 포스터 URL
*/
public interface ContestBoardExtractor<T> {

    String getTitle(T parser);
    LocalDate getStartReceptionDate(T parser);
    LocalDate getEndReceptionDate(T parser);
    String getHost(T parser);
    String getContent(T parser);
    String getMainImgUrl(T parser);
}
