package com.project.chagok.backend.scraper.batch.sitevisit;

import com.project.chagok.backend.scraper.domain.entitiy.Contest;
import com.project.chagok.backend.scraper.domain.repository.ContestRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContestKoreaVisitor implements SiteVisitor {

    private final ContestRepository contestRepository;
    private HashSet<Long> visitPages = null;

    @PostConstruct
    @Override
    public void init() {
        List<Contest> contestList = contestRepository.findAllInAMonth();

        visitPages = contestList.stream()
                .map(contest -> extractBoardIdx(contest.getSourceUrl()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Long extractBoardIdx(String url) {
        final String pageIdDel = "str_no=";
        return Long.parseLong(url.substring(url.lastIndexOf(pageIdDel) + pageIdDel.length()));
    }


    @Override
    public boolean isVisit(String url) {
        Long boardIdx = extractBoardIdx(url);

        if (visitPages.contains(boardIdx))
            return true;

        visitPages.add(boardIdx);
        return false;
    }

    @Override
    public boolean isVisit(LocalDateTime createdTime) {
        throw new UnsupportedOperationException();
    }
}
