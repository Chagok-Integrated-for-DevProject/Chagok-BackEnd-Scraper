package com.project.chagok.backend.scraper.batch.constants;

public enum ParsingUrlKey {

    HOLA("hola_parsing_url"), OKKY("okky_parsing_url"), INFLEARN("inf_parsing_url"), CONTEST_KOREA("contest_parsing_url");

    String key;

    private ParsingUrlKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
