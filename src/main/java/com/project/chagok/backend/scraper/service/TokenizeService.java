package com.project.chagok.backend.scraper.service;

import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import scala.collection.JavaConverters;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenizeService {

    public List<String> tokenizeKorAndEng(String inputStr) {

        // input string -> 문장 구성요소에 따른 token화, seq scala 자료구조 -> list 자료구조화
        List<KoreanTokenizer.KoreanToken> tokens = JavaConverters.seqAsJavaList(OpenKoreanTextProcessorJava.tokenize(inputStr));
        // 명사 및 알파벳 추출
        List<String> filteredTokens = tokens.stream().filter((token) -> (token.pos().toString().equals(KoreanPosJava.Noun.toString())) ||
                        token.pos().toString().equals(KoreanPosJava.Alpha.toString()))
                .map((token) -> {
                    if (token.pos().toString().equals(KoreanPosJava.Noun.toString()))
                        return token.text();
                    else if (token.pos().toString().equals(KoreanPosJava.Alpha.toString()))
                        return token.text().toLowerCase();
                    return null;
                })
                .collect(Collectors.toList());
        return filteredTokens;
    }
}
