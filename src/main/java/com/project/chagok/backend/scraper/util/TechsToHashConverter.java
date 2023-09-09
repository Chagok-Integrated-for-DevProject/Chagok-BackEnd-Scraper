package com.project.chagok.backend.scraper.util;


import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.FileSystemException;
import java.util.HashMap;


public class TechsToHashConverter {

    public static HashMap<String, String> getHashTechs() {

        HashMap<String, String> skillsMap = new HashMap<>();

        String filePath = "data/skills_dict.txt";
        try (InputStream inputStream = new ClassPathResource(filePath).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String parsingData;
            String skillKey = null;
            while ((parsingData = reader.readLine()) != null) {
                parsingData = parsingData.replace(" ", ""); // 띄어쓰기 제거
                if (parsingData.endsWith(":")) {
                    skillKey = parsingData.substring(0, parsingData.indexOf(":"));
                } else {
                    if (skillKey == null)
                        throw new FileSystemException("has no key");
                    skillsMap.put(parsingData, skillKey);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return skillsMap;
    }
}
