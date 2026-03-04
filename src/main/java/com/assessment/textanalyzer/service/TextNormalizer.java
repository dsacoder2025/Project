package com.assessment.textanalyzer.service;

import java.util.stream.Stream;

public class TextNormalizer {

    public Stream<String> normalize(Stream<String> lines) {
        if (lines == null)
            return Stream.empty();

        return lines.map(line -> line.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim());
    }
}
