package com.project.chagok.backend.scraper.batch.sitevisit;

import java.time.LocalDateTime;
import java.util.HashSet;

public interface SiteVisitor {


    public default boolean isVisit(String url) {
        throw new UnsupportedOperationException();
    }

    public default boolean isVisit(LocalDateTime createdTime) {
        throw new UnsupportedOperationException();
    }

    public void init();
}
