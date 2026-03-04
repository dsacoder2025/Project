package com.assessment;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.assessment.payroll.controller.PayrollController;
import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.EmployeeType;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.repository.EmployeeRepository;
import com.assessment.payroll.repository.InMemoryEmployeeRepository;
import com.assessment.payroll.service.PayrollProcessor;
import com.assessment.payroll.service.strategy.ContractorStrategy;
import com.assessment.payroll.service.strategy.DefaultTaxStrategy;
import com.assessment.payroll.service.strategy.FullTimeStrategy;
import com.assessment.payroll.service.strategy.PartTimeStrategy;
import com.assessment.payroll.service.strategy.PayStrategy;
import com.assessment.payroll.service.strategy.TaxStrategy;
import com.assessment.textanalyzer.controller.TextAnalyzerController;
import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.repository.FileTextRepository;
import com.assessment.textanalyzer.repository.TextRepository;
import com.assessment.textanalyzer.service.TextReader;
import com.assessment.textanalyzer.service.WordCounter;
import com.assessment.textanalyzer.service.export.FileReportExporter;
import com.assessment.textanalyzer.service.export.ReportExporter;
import com.assessment.textanalyzer.service.filter.LengthWordFilter;
import com.assessment.textanalyzer.service.filter.StopWordFilter;

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
                // Dependency Injection Setup
                EmployeeRepository employeeRepository = new InMemoryEmployeeRepository();

                Map<EmployeeType, PayStrategy> strategies = new HashMap<>();
                strategies.put(EmployeeType.FULL_TIME, new FullTimeStrategy());
                strategies.put(EmployeeType.PART_TIME, new PartTimeStrategy());
                strategies.put(EmployeeType.CONTRACTOR, new ContractorStrategy());

                TaxStrategy taxStrategy = new DefaultTaxStrategy();
                PayrollProcessor processor = new PayrollProcessor(strategies, taxStrategy);

                PayrollController payrollController = new PayrollController(employeeRepository, processor);

                // Pre-populate Data
                Employee e1 = Employee.builder().id("E001").name("Alice (FT, High)").fullTime().payRate(6000.0)
                                .withUnion().withRetirement().build();
                Employee e2 = Employee.builder().id("E002").name("Bob (FT, Low)").fullTime().payRate(2500.0).build();
                Employee e3 = Employee.builder().id("E003").name("Charlie (PT, Over Cap)").partTime().payRate(50.0)
                                .withRetirement().build();
                Employee e4 = Employee.builder().id("E004").name("David (PT, Normal)").partTime().payRate(40.0)
                                .withUnion().build();
                Employee e5 = Employee.builder().id("E005").name("Eve (Contractor)").contractor().payRate(300.0)
                                .build();
                Employee e6 = Employee.builder().id("E006").name("Frank (Contractor, Union)").contractor()
                                .payRate(250.0).withUnion().build();

                payrollController.addEmployee(e1);
                payrollController.addEmployee(e2);
                payrollController.addEmployee(e3);
                payrollController.addEmployee(e4);
                payrollController.addEmployee(e5);
                payrollController.addEmployee(e6);

                // Showcase editing employee via toBuilder:
                Employee initialEmployee = Employee.builder().id("E007").name("Grace (Trainee)").partTime()
                                .payRate(15.0).build();
                System.out.println("--- Employee Update Demo ---");
                System.out.println("Initial Employee: " + initialEmployee.getName() + " [Rate: "
                                + initialEmployee.getPayRate() + ", isUnion: " + initialEmployee.isUnionMember() + "]");

                Employee promotedEmployee = initialEmployee.toBuilder().name("Grace (Full Time, Union)").fullTime()
                                .payRate(300.0).withUnion().build();

                System.out.println("Updated Employee: " + promotedEmployee.getName() + " [Rate: "
                                + promotedEmployee.getPayRate() + ", isUnion: " + promotedEmployee.isUnionMember()
                                + "]");
                System.out.println("----- End Update -----");

                payrollController.addEmployee(promotedEmployee);

                // Define work units for the month
                Map<String, BigDecimal> workUnits = new HashMap<>();
                workUnits.put(e1.getId(), BigDecimal.ZERO);
                workUnits.put(e2.getId(), BigDecimal.ZERO);
                workUnits.put(e3.getId(), BigDecimal.valueOf(130.0)); // 130 hours -> should cap at 120
                workUnits.put(e4.getId(), BigDecimal.valueOf(80.0));
                workUnits.put(e5.getId(), BigDecimal.valueOf(20.0)); // 20 days
                workUnits.put(e6.getId(), BigDecimal.valueOf(10.0));
                workUnits.put(promotedEmployee.getId(), BigDecimal.ZERO);

                // Process Payroll
                List<PaySlip> slips = payrollController.processPayroll(workUnits);

                System.out.println("\nGenerated " + slips.size() + " PaySlips:");
                for (PaySlip slip : slips) {
                        System.out.println("--------------------------------------------------");
                        System.out.println(slip);
                }
                System.out.println("--------------------------------------------------");
        }

        private static void runTextAnalyzerDemo() throws IOException {
                // Dependency Injection Setup
                TextRepository textRepository = new FileTextRepository();
                TextReader textReader = new TextReader();
                WordCounter wordCounter = new WordCounter(List.of(
                                new LengthWordFilter(3),
                                new StopWordFilter()));
                ReportExporter reportExporter = new FileReportExporter();

                TextAnalyzerController controller = new TextAnalyzerController(textRepository, textReader, wordCounter,
                                reportExporter);

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

                // Make it 500+ words by repeating
                StringBuilder bigText = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                        bigText.append(sampleText).append("\n");
                }

                Path tempFile = Path.of("sample_text.txt");
                Files.writeString(tempFile, bigText.toString());
                System.out.println("Created sample file: " + tempFile.toAbsolutePath());

                // Process document
                StatisticsReport report = controller.analyzeDocument(tempFile.toString());

                System.out.println("Analysis Results:");
                System.out.println("Total Word Count: " + report.getTotalWordCount());
                System.out.println("Unique Word Count: " + report.getUniqueWordCount());
                System.out.println("Average Word Length: " + String.format("%.2f", report.getAverageWordLength()));
                System.out.println("Longest Word: " + report.getLongestWord());
                System.out.println("Most Frequent Word: " + report.getMostFrequentWord());

                // Export
                String exportPath = "analysis_report.txt";
                controller.exportReport(report, exportPath);
                System.out.println("\nReport exported to: " + exportPath);
        }
}
