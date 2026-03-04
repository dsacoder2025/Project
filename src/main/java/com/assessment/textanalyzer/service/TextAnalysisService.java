package com.assessment.textanalyzer.service;

import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.repository.DocumentRepository;
import com.assessment.textanalyzer.repository.ReportRepository;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public class TextAnalysisService {

    private final DocumentRepository documentRepository;
    private final ReportRepository reportRepository;
    private final TextNormalizer textNormalizer;
    private final WordCounter wordCounter;

    public TextAnalysisService(DocumentRepository documentRepository, ReportRepository reportRepository,
            TextNormalizer textNormalizer, WordCounter wordCounter) {
        this.documentRepository = documentRepository;
        this.reportRepository = reportRepository;
        this.textNormalizer = textNormalizer;
        this.wordCounter = wordCounter;
    }

    public StatisticsReport analyzeDocument(String documentIdentifier) throws IOException {
        Map<String, Integer> counts;
        try (Stream<String> lines = documentRepository.readLines(documentIdentifier)) {
            Stream<String> normalized = textNormalizer.normalize(lines);
            counts = wordCounter.countWords(normalized);
        }
        return new StatisticsReport(counts);
    }

    public void generateAndSaveReport(String documentIdentifier, String outputDestination) throws IOException {
        StatisticsReport report = analyzeDocument(documentIdentifier);
        reportRepository.saveReport(report, outputDestination);
    }
}
