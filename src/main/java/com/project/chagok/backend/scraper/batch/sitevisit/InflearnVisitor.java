package com.project.chagok.backend.scraper.batch.sitevisit;

import com.project.chagok.backend.scraper.constants.SiteType;
import com.project.chagok.backend.scraper.domain.entitiy.Project;
import com.project.chagok.backend.scraper.domain.entitiy.Study;
import com.project.chagok.backend.scraper.domain.repository.ProjectRepository;
import com.project.chagok.backend.scraper.domain.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class InflearnVisitor implements SiteVisitor{

    private final StudyRepository studyRepository;
    private final ProjectRepository projectRepository;
    private Long visitTimeIdx;

    @Override
    public boolean isVisit(String url) {
        Long boardId = extractBoardId(url);

        return visitTimeIdx >= boardId;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException();
    }

    public void studyInit() {
        Optional<Study> recentStudyData = studyRepository.findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType.INFLEARN);
        if (recentStudyData.isPresent())
            visitTimeIdx = extractBoardId(recentStudyData.get().getSourceUrl());
        else
            visitTimeIdx = 0L;
    }

    public void projectInit() {
        Optional<Project> recentStudyData = projectRepository.findFirstBySiteTypeOrderByCreatedTimeDesc(SiteType.INFLEARN);
        if (recentStudyData.isPresent())
            visitTimeIdx = extractBoardId(recentStudyData.get().getSourceUrl());
        else
            visitTimeIdx = 0L;
    }

    // 게시글 id 추출
    private Long extractBoardId(String url) {
        String pattern = "/(?:studies|projects)/(\\d+)";

        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(url);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }
}
