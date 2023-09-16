package com.project.chagok.backend.scraper.domain.entitiy;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

//comment,scrap은 글이 삭제된다면 남아있을 필요가 없음
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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