package com.assessment.textanalyzer.service.filter;

import java.util.Set;

public class StopWordFilter implements WordFilter {
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "is", "at", "which", "on", "a", "an", "as", "are",
            "was", "were", "been", "be", "have", "has", "had", "do", "does", "did",
            "of", "to", "in", "for", "with", "it", "that", "this", "from", "by");

    @Override
    public boolean isValid(String word) {
        return word != null && !STOP_WORDS.contains(word.toLowerCase());
    }
}
