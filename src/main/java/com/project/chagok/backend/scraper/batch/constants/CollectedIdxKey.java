package com.project.chagok.backend.scraper.batch.constants;

public enum CollectedIdxKey {

    HOLA("hola_collected_idx_key"), OKKY("okky_collected_idx_key"), INFLEARN_STUDY("inflearn_study_collected_idx_key"),
    INFLEARN_PROJECT("inflearn_project_collected_idx_key"), CONTEST_KOREA("contest_korea_collected_idx_key");

    String key;

    private CollectedIdxKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
