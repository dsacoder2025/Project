package com.assessment.textanalyzer.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCounter {

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "is", "at", "which", "on", "a", "an", "as", "are",
            "was", "were", "been", "be", "have", "has", "had", "do", "does", "did",
            "of", "to", "in", "for", "with", "it", "that", "this", "from", "by");

    public Map<String, Integer> countWords(Stream<String> normalizedLines) {
        Map<String, Integer> frequencyMap = new ConcurrentHashMap<>();

        if (normalizedLines == null) {
            return frequencyMap;
        }

        normalizedLines.parallel().forEach(line -> {
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (word.length() >= 3 && !STOP_WORDS.contains(word)) {
                    frequencyMap.merge(word, 1, Integer::sum);
                }
            }
        });

        return frequencyMap;
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
