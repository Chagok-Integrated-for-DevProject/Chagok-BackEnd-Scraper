package com.project.chagok.backend.scraper.batch.tasklet;

import com.project.chagok.backend.scraper.batch.constants.JobSiteType;
import com.project.chagok.backend.scraper.batch.sitevisit.ContestKoreaVisitor;
import com.project.chagok.backend.scraper.constants.TimeDelay;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

@Component
public class ContestKoreaURLExtractor extends URLExtractorBase{

    public ContestKoreaURLExtractor(ContestKoreaVisitor visitor) {
        super(visitor);
    }

    @Override
    List<String> extractURL(JobSiteType jobSiteType) {
        final String scarpListUrl = "https://www.contestkorea.com/sub/list.php?displayrow=100&Txt_sortkey=a.str_aedate&page=1&Txt_bcode=030510001";

        List<String> willParseUrls = new ArrayList<>();

        Document parser = null;
        try {
            parser = Jsoup
                    .connect(scarpListUrl)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 리스트 a태그 셀렉터
        final String ListLinkSelector = ".list_style_2 .title a";
        Elements listElements = parser.select(ListLinkSelector);

        for (Element listItem : listElements) {
            String scrapItemUrl = listItem.absUrl("href");
            if (isVisit(scrapItemUrl)) {
                continue;
            }

            willParseUrls.add(scrapItemUrl);
        }

        return willParseUrls;
    }
}
