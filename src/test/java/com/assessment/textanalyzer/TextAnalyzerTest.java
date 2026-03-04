package com.assessment.textanalyzer;

import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.service.TextNormalizer;
import com.assessment.textanalyzer.service.WordCounter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TextAnalyzerTest {

    @Test
    public void testNormalizationAndCounting(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        String content = "Hello, world! This is a test. World is correct.";
        Files.writeString(file, content);

        TextNormalizer normalizer = new TextNormalizer();
        String normalized;
        try (java.util.stream.Stream<String> stream = normalizer.normalize(Files.lines(file))) {
            normalized = stream.collect(java.util.stream.Collectors.joining(" "));
        }

        // "hello world this is a test world is correct" (spaces might vary but trimmed)
        // normalized should be lowercase alphanumeric
        assertFalse(normalized.contains(","));
        assertFalse(normalized.contains("!"));
        assertTrue(normalized.startsWith("hello"));

        WordCounter counter = new WordCounter();
        Map<String, Integer> counts = counter.countWords(normalized);

        // "hello" -> 1
        // "world" -> 2
        // "this" -> 1 (length 4 > 3, not stop word?) 'this' is stop word in my list
        // "is", "a" -> stop words
        // "test" -> 1
        // "correct" -> 1

        assertEquals(2, counts.get("world"));
        assertFalse(counts.containsKey("is")); // stop word
        assertFalse(counts.containsKey("a")); // stop word
        assertTrue(counts.containsKey("test"));

        // Stats
        StatisticsReport report = new StatisticsReport(counts);
        assertEquals("world", report.getMostFrequentWord()); // 2 times
        assertEquals("correct", report.getLongestWord()); // 7 chars
    }

    @Test
    public void testStopWords() {
        WordCounter counter = new WordCounter();
        String text = "the quick brown fox jumps over the lazy dog";
        // the: stop
        // quick: keep
        // brown: keep
        // fox: keep (3 chars)
        // jumps: keep
        // over: keep
        // lazy: keep
        // dog: keep

        Map<String, Integer> counts = counter.countWords(text);
        assertFalse(counts.containsKey("the"));
        assertTrue(counts.containsKey("quick"));
        assertTrue(counts.containsKey("dog"));
    }

    @Test
    public void testShortWords() {
        WordCounter counter = new WordCounter();
        String text = "hi my name is bo";
        // hi (2), my (2), name (4), is (stop), bo (2)
        // only 'name' should remain? or 'name' and 'is' (stop)?
        // 'is' is stop.
        // 'hi', 'my', 'bo' are < 3.

        Map<String, Integer> counts = counter.countWords(text);
        assertEquals(1, counts.size());
        assertTrue(counts.containsKey("name"));
    }

    @Test
    public void testCaseInsensitivityAndPunctuation() {
        TextNormalizer normalizer = new TextNormalizer();
        WordCounter counter = new WordCounter();

        // Mixed case and punctuation
        java.util.stream.Stream<String> lines = java.util.stream.Stream.of("Java! jAvA, JAVA.");
        String normalized = normalizer.normalize(lines).collect(java.util.stream.Collectors.joining(" "));

        Map<String, Integer> counts = counter.countWords(normalized);
        assertEquals(1, counts.size(), "Should only contain one unique word");
        assertEquals(3, counts.get("java"), "Java should be counted 3 times regardless of case");
    }

    @Test
    public void testEmptyAndSpecialCharacters() {
        TextNormalizer normalizer = new TextNormalizer();
        WordCounter counter = new WordCounter();

        // Only special characters
        java.util.stream.Stream<String> lines = java.util.stream.Stream.of("!@# $%^ &*()");
        String normalized = normalizer.normalize(lines).collect(java.util.stream.Collectors.joining(" "));

        Map<String, Integer> counts = counter.countWords(normalized);
        assertTrue(counts.isEmpty(), "Counts should be empty for special characters only");

        // Verify StatisticsReport handles empty graphs gracefully (no divide-by-zero)
        StatisticsReport report = new StatisticsReport(counts);
        assertEquals(0, report.getTotalWordCount());
        assertEquals(0, report.getUniqueWordCount());
        assertEquals(0.0, report.getAverageWordLength());
        assertEquals("", report.getLongestWord());
        assertEquals("", report.getMostFrequentWord());
    }

    @Test
    public void testStopWordsAsSubstrings() {
        WordCounter counter = new WordCounter();
        // The word "issue" contains the stop word "is", and "bland" contains "and"
        // Ensure our boundary logic doesn't eagerly cull substrings
        String text = "the issue is bland and boring";
        Map<String, Integer> counts = counter.countWords(text);

        assertFalse(counts.containsKey("the"));
        assertFalse(counts.containsKey("is"));
        assertFalse(counts.containsKey("and"));

        assertTrue(counts.containsKey("issue")); // Has "is"
        assertTrue(counts.containsKey("bland")); // Has "and"
        assertTrue(counts.containsKey("boring"));
    }

    @Test
    public void testCompletelyBlankDocumentAndWhitespaces() {
        TextNormalizer normalizer = new TextNormalizer();
        WordCounter counter = new WordCounter();

        java.util.stream.Stream<String> lines = java.util.stream.Stream.of("   ", "\t\t", " \n \r ");
        String normalized = normalizer.normalize(lines).collect(java.util.stream.Collectors.joining(" "));

        Map<String, Integer> counts = counter.countWords(normalized);
        assertTrue(counts.isEmpty(), "Counts should be empty for a whitespace-only document");
    }

    @Test
    public void testExtremelyLongWordPerformance() {
        WordCounter counter = new WordCounter();

        // Generate a 10,000 character string with no spaces
        String longWord = "a".repeat(10000);
        String text = "hello " + longWord + " world";

        Map<String, Integer> counts = counter.countWords(text);

        assertTrue(counts.containsKey(longWord));
        assertEquals(1, counts.get(longWord));
        assertEquals(3, counts.size()); // hello, longWord, world

        StatisticsReport report = new StatisticsReport(counts);
        assertEquals(longWord, report.getLongestWord());
    }
}
