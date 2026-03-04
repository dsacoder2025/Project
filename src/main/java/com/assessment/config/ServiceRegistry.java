package com.assessment.config;

import com.assessment.payroll.repository.EmployeeRepository;
import com.assessment.payroll.repository.InMemoryEmployeeRepository;
import com.assessment.payroll.service.PayrollProcessor;
import com.assessment.payroll.service.PayrollService;
import com.assessment.textanalyzer.repository.DocumentRepository;
import com.assessment.textanalyzer.repository.FileDocumentRepository;
import com.assessment.textanalyzer.repository.FileReportRepository;
import com.assessment.textanalyzer.repository.ReportRepository;
import com.assessment.textanalyzer.service.TextAnalysisService;
import com.assessment.textanalyzer.service.TextNormalizer;
import com.assessment.textanalyzer.service.WordCounter;

/**
 * Acts as a simple Dependency Injection (DI) Container or Factory.
 * Centralizes the wiring of components to remove object instantiation from Main
 * (adhering to SRP and DIP).
 * In a large enterprise application, frameworks like Spring or Google Guice
 * handle this automatically.
 */
public class ServiceRegistry {

    private static final ServiceRegistry instance = new ServiceRegistry();

    // Reusable, stateless domain services and repositories
    private final PayrollProcessor payrollProcessor;
    private final DocumentRepository documentRepository;
    private final ReportRepository reportRepository;
    private final TextNormalizer textNormalizer;
    private final WordCounter wordCounter;
    private final TextAnalysisService textAnalysisService;

    private ServiceRegistry() {
        this.payrollProcessor = new PayrollProcessor();
        this.documentRepository = new FileDocumentRepository();
        this.reportRepository = new FileReportRepository();
        this.textNormalizer = new TextNormalizer();
        this.wordCounter = new WordCounter();
        this.textAnalysisService = new TextAnalysisService(documentRepository, reportRepository, textNormalizer,
                wordCounter);
    }

    public static ServiceRegistry getInstance() {
        return instance;
    }

    /**
     * Creates a new PayrollService with a fresh isolated InMemory Repository.
     * We don't use a singleton repository here out-of-the-box so multiple demos
     * or distinct system runs start with a fresh slate.
     */
    public PayrollService createPayrollService() {
        EmployeeRepository employeeRepository = new InMemoryEmployeeRepository();
        return new PayrollService(employeeRepository, payrollProcessor);
    }

    public TextAnalysisService getTextAnalysisService() {
        return textAnalysisService;
    }

    public DocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public TextNormalizer getTextNormalizer() {
        return textNormalizer;
    }

    public WordCounter getWordCounter() {
        return wordCounter;
    }
}
