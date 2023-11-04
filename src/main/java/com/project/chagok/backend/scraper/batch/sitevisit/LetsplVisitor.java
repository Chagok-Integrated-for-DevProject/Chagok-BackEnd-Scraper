package com.project.chagok.backend.scraper.batch.sitevisit;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Contest;
import com.project.chagok.backend.scraper.domain.entitiy.Project;
import com.project.chagok.backend.scraper.domain.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LetsplVisitor implements SiteVisitor{

    private final ProjectRepository projectRepository;
    private HashSet<String> visitPages = null;

    @PostConstruct
    @Override
    public void init() {
        List<Project> proejectList = projectRepository.findBySiteType(SiteType.LETSPL);

        visitPages = proejectList.stream()
                .map(contest -> extractBoardIdx(contest.getSourceUrl()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String extractBoardIdx(String url) {
        final String pageIdDel = "project/";
        return url.substring(url.lastIndexOf(pageIdDel) + pageIdDel.length());
    }

    @Override
    public boolean isVisit(String proejctId) {

        if (visitPages.contains(proejctId))
            return true;

        visitPages.add(proejctId);
        return false;
    }
}
