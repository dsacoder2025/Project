package com.assessment.textanalyzer.service;

import java.util.stream.Stream;

public class TextReader {

    public Stream<String> readAndNormalize(Stream<String> lines) {
        if (lines == null) {
            return Stream.empty();
        }
        return lines.map(line -> line.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim());
    }
}
