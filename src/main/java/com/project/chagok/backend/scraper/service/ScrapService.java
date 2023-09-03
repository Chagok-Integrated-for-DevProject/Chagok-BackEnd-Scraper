package com.project.chagok.backend.scraper.service;

import com.project.chagok.backend.scraper.constants.CategoryType;
import com.project.chagok.backend.scraper.dao.ContestDao;
import com.project.chagok.backend.scraper.dao.ProjectDao;
import com.project.chagok.backend.scraper.dao.StudyDao;
import com.project.chagok.backend.scraper.domain.entitiy.Contest;
import com.project.chagok.backend.scraper.domain.entitiy.Project;
import com.project.chagok.backend.scraper.domain.entitiy.Study;
import com.project.chagok.backend.scraper.dto.ContestDto;
import com.project.chagok.backend.scraper.dto.StudyProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapService {

    /*
    contest, project, study 저장하기 위한 service
     */

    private final ContestDao contestDao;
    private final ProjectDao projectDao;
    private final StudyDao studyDao;

    // contest 저장
    public void saveContest(ContestDto contestDto) {
         Contest contest = Contest.builder()
                 .host(contestDto.getHost())
                 .content(contestDto.getContent())
                 .imageUrl(contestDto.getImgUrl())
                 .title(contestDto.getTitle())
                 .sourceUrl(contestDto.getUrl())
                 .startDate(contestDto.getStartDate())
                 .endDate(contestDto.getEndDate())
                 .scrapCount(0)
                 .viewCount(0)
                 .hotCount(0)
                 .commentCount(0)
                 .build();

         contestDao.save(contest);
    }

    // study인지 project인지 구분하여 저장
    public void saveStudyProject(StudyProjectDto studyProjectDto) {
        if (studyProjectDto.getCategoryType() == CategoryType.PROJECT) {
            Project project = Project.builder()
                    .createdTime(studyProjectDto.getCreatedDate())
                    .siteType(studyProjectDto.getSiteType())
                    .nickname(studyProjectDto.getNickname())
                    .techStacks(studyProjectDto.getTechList())
                    .title(studyProjectDto.getTitle())
                    .sourceUrl(studyProjectDto.getSourceUrl())
                    .content(studyProjectDto.getContent())
                    .viewCount(0)
                    .hotCount(0)
                    .scrapCount(0)
                    .build();

            projectDao.save(project);
        } else {
            Study study = Study.builder()
                    .createdTime(studyProjectDto.getCreatedDate())
                    .siteType(studyProjectDto.getSiteType())
                    .nickname(studyProjectDto.getNickname())
                    .techStacks(studyProjectDto.getTechList())
                    .title(studyProjectDto.getTitle())
                    .sourceUrl(studyProjectDto.getSourceUrl())
                    .content(studyProjectDto.getContent())
                    .viewCount(0)
                    .hotCount(0)
                    .scrapCount(0)
                    .build();

            studyDao.save(study);
        }
    }
}
