package com.assessment.textanalyzer.service;

import com.assessment.textanalyzer.service.filter.WordFilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCounter {

    private final List<WordFilter> filters;

    public WordCounter(List<WordFilter> filters) {
        this.filters = filters != null ? filters : new ArrayList<>();
    }

    public Map<String, Integer> countWords(Stream<String> normalizedLines) {
        Map<String, Integer> frequencyMap = new ConcurrentHashMap<>();

        if (normalizedLines == null) {
            return frequencyMap;
        }

        normalizedLines.parallel().forEach(line -> {
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (isValidWord(word)) {
                    frequencyMap.merge(word, 1, Integer::sum);
                }
            }
        });

        return frequencyMap;
    }

    private boolean isValidWord(String word) {
        for (WordFilter filter : filters) {
            if (!filter.isValid(word)) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Integer> countWords(String normalizedText) {
        if (normalizedText == null || normalizedText.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        return countWords(Stream.of(normalizedText));
    }

    public List<Map.Entry<String, Integer>> getTopNWords(Map<String, Integer> frequencyMap, int n) {
        return frequencyMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<String> getWordsStartingWith(Map<String, Integer> frequencyMap, String prefix) {
        if (prefix == null)
            return Collections.emptyList();
        String p = prefix.toLowerCase();
        return frequencyMap.keySet().stream()
                .filter(w -> w.startsWith(p))
                .sorted()
                .collect(Collectors.toList());
    }
}
