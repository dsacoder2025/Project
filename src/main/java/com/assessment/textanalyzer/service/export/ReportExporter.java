package com.assessment.textanalyzer.service.export;

import com.assessment.textanalyzer.model.StatisticsReport;
import java.io.IOException;

public interface ReportExporter {
    void export(StatisticsReport report, String destination) throws IOException;
}
