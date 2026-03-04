package com.assessment.textanalyzer.repository;

import com.assessment.textanalyzer.model.StatisticsReport;
import java.io.IOException;

public interface ReportRepository {
    void saveReport(StatisticsReport report, String destination) throws IOException;
}
