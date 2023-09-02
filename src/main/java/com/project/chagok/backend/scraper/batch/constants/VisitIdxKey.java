package com.project.chagok.backend.scraper.batch.constants;

public enum VisitIdxKey {

    HOLA("hola_visit_idx_key"), OKKY("okky_visit_idx_key"), INFLEARN_STUDY("inflearn_study_visit_idx_key"), INFLEARN_PROJECT("inflearn_project_visit_idx_key");

    String key;

    private VisitIdxKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
