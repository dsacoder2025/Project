package com.assessment;

import com.assessment.payroll.model.Employee;
import com.assessment.payroll.model.EmployeeType;
import com.assessment.payroll.model.PaySlip;
import com.assessment.payroll.service.PayrollProcessor;
import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.service.TextReader;
import com.assessment.textanalyzer.service.WordCounter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        PayrollProcessor processor = new PayrollProcessor();
        Map<Employee, BigDecimal> employeeData = new HashMap<>();

        // 1. Full Time, High Salary
        employeeData.put(
            Employee.builder()
                .id("E001").name("Alice (FT, High)")
                .fullTime().payRate(6000.0)
                .withUnion().withRetirement()
                .build(), 
            BigDecimal.ZERO // Hours irrelevant
        );

        // 2. Full Time, Low Salary
        employeeData.put(
            Employee.builder()
                .id("E002").name("Bob (FT, Low)")
                .fullTime().payRate(2500.0)
                .build(), 
            BigDecimal.ZERO
        );

        // 3. Part Time, Over Cap
        employeeData.put(
            Employee.builder()
                .id("E003").name("Charlie (PT, Over Cap)")
                .partTime().payRate(50.0)
                .withRetirement()
                .build(), 
            BigDecimal.valueOf(130.0) // 130 hours -> should cap at 120
        );

        // 4. Part Time, Under Cap
        employeeData.put(
            Employee.builder()
                .id("E004").name("David (PT, Normal)")
                .partTime().payRate(40.0)
                .withUnion()
                .build(),
            BigDecimal.valueOf(80.0)
        );

        // 5. Contractor, Long duration
        employeeData.put(
            Employee.builder()
                .id("E005").name("Eve (Contractor)")
                .contractor().payRate(300.0)
                .build(),
            BigDecimal.valueOf(20.0) // 20 days
        );

        // 6. Contractor, Union Member
        employeeData.put(
            Employee.builder()
                .id("E006").name("Frank (Contractor, Union)")
                .contractor().payRate(250.0)
                .withUnion()
                .build(), 
            BigDecimal.valueOf(10.0)
        );

        List<PaySlip> slips = processor.processMonthlyPayroll(employeeData);
        
        System.out.println("Generated " + slips.size() + " PaySlips:");
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

        // Process
        TextReader reader = new TextReader();
        WordCounter counter = new WordCounter();
        
        String normalized = reader.readAndNormalize(tempFile.toString());
        Map<String, Integer> counts = counter.countWords(normalized);
        StatisticsReport report = new StatisticsReport(counts);

        System.out.println("Analysis Results:");
        System.out.println("Total Word Count: " + report.getTotalWordCount());
        System.out.println("Unique Word Count: " + report.getUniqueWordCount());
        System.out.println("Average Word Length: " + String.format("%.2f", report.getAverageWordLength()));
        System.out.println("Longest Word: " + report.getLongestWord());
        System.out.println("Most Frequent Word: " + report.getMostFrequentWord());

        System.out.println("\nTop 5 Words:");
        List<Map.Entry<String, Integer>> top5 = counter.getTopNWords(counts, 5);
        top5.forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        System.out.println("\nWords starting with 'pro':");
        List<String> proWords = counter.getWordsStartingWith(counts, "pro");
        System.out.println(proWords);

        // Export
        String exportPath = "analysis_report.txt";
        report.exportReport(exportPath);
        System.out.println("\nReport exported to: " + exportPath);
    }
}
