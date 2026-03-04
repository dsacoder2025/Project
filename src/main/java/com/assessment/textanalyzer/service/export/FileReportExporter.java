package com.assessment.textanalyzer.service.export;

import com.assessment.textanalyzer.model.StatisticsReport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReportExporter implements ReportExporter {

    @Override
    public void export(StatisticsReport report, String destination) throws IOException {
        String reportContent = String.format("""
                Text Analysis Report
                ====================
                Total Word Count: %d
                Unique Word Count: %d
                Average Word Length: %.2f
                Longest Word: %s
                Most Frequent Word: %s
                """,
                report.getTotalWordCount(),
                report.getUniqueWordCount(),
                report.getAverageWordLength(),
                report.getLongestWord(),
                report.getMostFrequentWord());

        Files.writeString(Path.of(destination), reportContent);
    }
}
