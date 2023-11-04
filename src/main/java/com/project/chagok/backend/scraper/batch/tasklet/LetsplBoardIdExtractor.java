package com.project.chagok.backend.scraper.batch.tasklet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.dto.LetsplSearchDto;
import com.project.chagok.backend.scraper.batch.sitevisit.LetsplVisitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LetsplBoardIdExtractor extends URLExtractorBase{
    public LetsplBoardIdExtractor(LetsplVisitor visitor) {
        super(visitor);
    }

    private final String letsplBoardUrl = "https://letspl.me/find_project/search";

    @Override
    List<String> extractURL(JobSiteType jobSiteType) throws Exception{

        LetsplSearchDto letsplSearchDto = new LetsplSearchDto(0);
        ObjectMapper om = new ObjectMapper();

        String reqBoardJsonStr = om.writeValueAsString(letsplSearchDto);

        Document parser = Jsoup
                .connect(letsplBoardUrl)
                .header("Content-Type", "application/json")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .requestBody(reqBoardJsonStr)
                .ignoreContentType(true)
                .post();

        String resJsonStr = parser.body().text();

        JsonNode projectListJson = om.readTree(resJsonStr).get("project_info");

        List<String> projectNoList = new ArrayList<>();

        for (var projectJson : projectListJson) {
            String projectId = projectJson.get("PROJECT_NO").asText();
            String newProjectCode = projectJson.get("NEW_PROJECT").asText();

            if (!isNewProject(newProjectCode) || isVisit(projectId)) {
                continue;
            }

            projectNoList.add(projectId);
        }

        return projectNoList;
    }

    public boolean isNewProject(String newProjectCode) {
        return newProjectCode.equals("01");
    }
}
