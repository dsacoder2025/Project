package com.assessment.textanalyzer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;

public class StatisticsReport {
    private int totalWordCount;
    private int uniqueWordCount;
    private double averageWordLength;
    private String longestWord;
    private String mostFrequentWord;

    public StatisticsReport(Map<String, Integer> frequencyMap) {
        calculateStats(frequencyMap);
    }

    private void calculateStats(Map<String, Integer> map) {
        this.uniqueWordCount = map.size();
        
        long totalLength = 0;
        int totalCount = 0;
        String curLeadingWord = "";
        int maxFreq = -1;
        String curMostFreqWord = "";

        
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();

            totalCount += count;
            totalLength += (long) word.length() * count;

            if (word.length() > curLeadingWord.length()) {
                curLeadingWord = word;
            }

            if (count > maxFreq) {
                maxFreq = count;
                curMostFreqWord = word;
            }
        }

        this.totalWordCount = totalCount;
        this.longestWord = curLeadingWord;
        this.mostFrequentWord = curMostFreqWord;
        this.averageWordLength = totalCount == 0 ? 0 : (double) totalLength / totalCount;
    }

    public void exportReport(String outputFilePath) throws IOException {
        String report = String.format("""
            Text Analysis Report
            ====================
            Total Word Count: %d
            Unique Word Count: %d
            Average Word Length: %.2f
            Longest Word: %s
            Most Frequent Word: %s
            """, 
            totalWordCount, uniqueWordCount, averageWordLength, longestWord, mostFrequentWord);
            
        Files.writeString(Path.of(outputFilePath), report);
    }

    public int getTotalWordCount() { return totalWordCount; }
    public int getUniqueWordCount() { return uniqueWordCount; }
    public double getAverageWordLength() { return averageWordLength; }
    public String getLongestWord() { return longestWord; }
    public String getMostFrequentWord() { return mostFrequentWord; }
}
