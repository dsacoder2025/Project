package com.assessment;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.assessment.config.ServiceRegistry;
import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.service.PayrollService;
import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.repository.DocumentRepository;
import com.assessment.textanalyzer.service.TextAnalysisService;
import com.assessment.textanalyzer.service.TextNormalizer;
import com.assessment.textanalyzer.service.WordCounter;

public class Main {

        public static void main(String[] args) {
                System.out.println("=== Tech Assessment Demonstration ===");

                // ---------------------------------------------------------
                // PART 1: Payroll Calculator
                // ---------------------------------------------------------
                System.out.println("\n--- Part 1: Payroll Calculator ---");
                runPayrollDemo();

                // ---------------------------------------------------------
                // PART 2: Text Document Analyzer
                // ---------------------------------------------------------
                System.out.println("\n--- Part 2: Text Document Analyzer ---");
                try {
                        runTextAnalyzerDemo();
                } catch (IOException e) {
                        System.err.println("Error running text analyzer demo: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        private static void runPayrollDemo() {
                PayrollService payrollService = ServiceRegistry.getInstance().createPayrollService();

                Map<String, BigDecimal> employeeWorkUnits = new HashMap<>();

                // 1. Full Time, High Salary
                Employee e1 = Employee.builder()
                                .id("E001").name("Alice (FT, High)")
                                .fullTime().payRate(6000.0)
                                .withUnion().withRetirement()
                                .build();
                payrollService.registerEmployee(e1);
                employeeWorkUnits.put(e1.getId(), BigDecimal.ZERO); // Hours irrelevant

                // 2. Full Time, Low Salary
                Employee e2 = Employee.builder()
                                .id("E002").name("Bob (FT, Low)")
                                .fullTime().payRate(2500.0)
                                .build();
                payrollService.registerEmployee(e2);
                employeeWorkUnits.put(e2.getId(), BigDecimal.ZERO);

                // 3. Part Time, Over Cap
                Employee e3 = Employee.builder()
                                .id("E003").name("Charlie (PT, Over Cap)")
                                .partTime().payRate(50.0)
                                .withRetirement()
                                .build();
                payrollService.registerEmployee(e3);
                employeeWorkUnits.put(e3.getId(), BigDecimal.valueOf(130.0)); // 130 hours -> should cap at 120

                // 4. Part Time, Under Cap
                Employee e4 = Employee.builder()
                                .id("E004").name("David (PT, Normal)")
                                .partTime().payRate(40.0)
                                .withUnion()
                                .build();
                payrollService.registerEmployee(e4);
                employeeWorkUnits.put(e4.getId(), BigDecimal.valueOf(80.0));

                // 5. Contractor, Long duration
                Employee e5 = Employee.builder()
                                .id("E005").name("Eve (Contractor)")
                                .contractor().payRate(300.0)
                                .build();
                payrollService.registerEmployee(e5);
                employeeWorkUnits.put(e5.getId(), BigDecimal.valueOf(20.0)); // 20 days

                // 6. Contractor, Union Member
                Employee e6 = Employee.builder()
                                .id("E006").name("Frank (Contractor, Union)")
                                .contractor().payRate(250.0)
                                .withUnion()
                                .build();
                payrollService.registerEmployee(e6);
                employeeWorkUnits.put(e6.getId(), BigDecimal.valueOf(10.0));

                // Showcase editing employee via toBuilder:
                Employee initialEmployee = Employee.builder()
                                .id("E007").name("Grace (Trainee)").partTime().payRate((15.0)).build();
                System.out.println("--- Employee Update Demo ---");
                System.out.println("Initial Employee: " + initialEmployee.getName() + " [Rate: "
                                + initialEmployee.getPayRate() + ", isUnion: " + initialEmployee.isUnionMember() + "]");

                Employee promotedEmployee = initialEmployee.toBuilder()
                                .name("Grace (Full Time, Union)")
                                .fullTime()
                                .payRate(150.0)
                                .withUnion()
                                .build();

                System.out.println("Updated Employee: " + promotedEmployee.getName() + " [Rate: "
                                + promotedEmployee.getPayRate() + ", isUnion: " + promotedEmployee.isUnionMember()
                                + "]");
                System.out.println("----- End Update -----");
                payrollService.registerEmployee(promotedEmployee); // Add to processing system too!
                employeeWorkUnits.put(promotedEmployee.getId(), BigDecimal.ZERO);

                List<PaySlip> slips = payrollService.processPayroll(employeeWorkUnits);

                System.out.println("\nGenerated " + slips.size() + " PaySlips:");
                for (PaySlip slip : slips) {
                        System.out.println("--------------------------------------------------");
                        System.out.println(slip);
                }
                System.out.println("--------------------------------------------------");
        }

        private static void runTextAnalyzerDemo() throws IOException {
                // Create a sample file
                String sampleText = """
                                The quick brown fox jumps over the lazy dog.
                                The dog was not amused by the fox's antics.
                                Java is a programming language. Java is widely used.
                                Programming in Java is fun and challenging.
                                Analysis of text documents is a common task in computer science.
                                Common tasks include counting words and finding specific patterns.
                                This text document analyzer should handle various cases efficiently.
                                Efficiency is key when processing large volumes of text.
                                The analyzer must exclude common stop words like the, and, is.
                                It should also ignore short words with fewer than 3 characters.
                                Let's see if it works as expected!
                                One two three four five six seven eight nine ten.
                                Repeat repeat repeat.
                                """;

                // Make it 500+ words by repeating?
                StringBuilder bigText = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                        bigText.append(sampleText).append("\n");
                }

                Path tempFile = Path.of("sample_text.txt");
                Files.writeString(tempFile, bigText.toString());
                System.out.println("Created sample file: " + tempFile.toAbsolutePath());

                // Process via ServiceRegistry
                ServiceRegistry registry = ServiceRegistry.getInstance();
                TextAnalysisService analysisService = registry.getTextAnalysisService();

                StatisticsReport report = analysisService.analyzeDocument(tempFile.toString());

                System.out.println("Analysis Results:");
                System.out.println("Total Word Count: " + report.getTotalWordCount());
                System.out.println("Unique Word Count: " + report.getUniqueWordCount());
                System.out.println("Average Word Length: " + String.format("%.2f", report.getAverageWordLength()));
                System.out.println("Longest Word: " + report.getLongestWord());
                System.out.println("Most Frequent Word: " + report.getMostFrequentWord());

                // We need the counts map to demo the counter methods. This wasn't exposed by
                // the report directly.
                // For demonstration, let's re-count here or expose it. Since it's a demo, we
                // will just count a stream.
                DocumentRepository docRepo = registry.getDocumentRepository();
                TextNormalizer normalizer = registry.getTextNormalizer();
                WordCounter counter = registry.getWordCounter();

                Map<String, Integer> counts;
                try (java.util.stream.Stream<String> normalized = normalizer
                                .normalize(docRepo.readLines(tempFile.toString()))) {
                        counts = counter.countWords(normalized);
                }

                System.out.println("\nTop 5 Words:");
                List<Map.Entry<String, Integer>> top5 = counter.getTopNWords(counts, 5);
                top5.forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

                System.out.println("\nWords starting with 'pro':");
                List<String> proWords = counter.getWordsStartingWith(counts, "pro");
                System.out.println(proWords);

                // Export
                String exportPath = "analysis_report.txt";
                analysisService.generateAndSaveReport(tempFile.toString(), exportPath);
                System.out.println("\nReport exported to: " + exportPath);
        }
}
