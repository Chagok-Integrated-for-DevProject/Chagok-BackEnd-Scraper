package com.project.chagok.backend.scraper.batch.sitevisit;

import java.time.LocalDateTime;
import java.util.HashSet;

public interface SiteVisitor {


    public boolean isVisit(String url);
    public boolean isVisit(LocalDateTime createdTime);

    public void init();
}
