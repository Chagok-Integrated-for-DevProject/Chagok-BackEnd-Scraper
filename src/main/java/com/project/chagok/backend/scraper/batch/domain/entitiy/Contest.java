package com.project.chagok.backend.scraper.batch.domain.entitiy;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

//comment,scrap은 글이 삭제된다면 남아있을 필요가 없음
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    private String host;

    private String content;

    private String imageUrl;

    private String sourceUrl;

    private int hotCount;

    private int scrapCount;

    private int viewCount;

    private int commentCount;

}