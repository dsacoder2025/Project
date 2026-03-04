package com.assessment.textanalyzer.controller;

import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.repository.TextRepository;
import com.assessment.textanalyzer.service.TextReader;
import com.assessment.textanalyzer.service.WordCounter;
import com.assessment.textanalyzer.service.export.ReportExporter;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public class TextAnalyzerController {

    private final TextRepository textRepository;
    private final TextReader textReader;
    private final WordCounter wordCounter;
    private final ReportExporter reportExporter;

    public TextAnalyzerController(TextRepository textRepository, TextReader textReader, WordCounter wordCounter,
            ReportExporter reportExporter) {
        this.textRepository = textRepository;
        this.textReader = textReader;
        this.wordCounter = wordCounter;
        this.reportExporter = reportExporter;
    }

    public StatisticsReport analyzeDocument(String sourceFilePath) throws IOException {
        try (Stream<String> lines = textRepository.readLines(sourceFilePath)) {
            Stream<String> normalizedLines = textReader.readAndNormalize(lines);
            Map<String, Integer> wordCounts = wordCounter.countWords(normalizedLines);
            return new StatisticsReport(wordCounts);
        }
    }

    public void exportReport(StatisticsReport report, String destinationFilePath) throws IOException {
        reportExporter.export(report, destinationFilePath);
    }
}
