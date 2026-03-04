package com.assessment.textanalyzer;

import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.service.TextReader;
import com.assessment.textanalyzer.service.WordCounter;
import com.assessment.textanalyzer.service.filter.LengthWordFilter;
import com.assessment.textanalyzer.service.filter.StopWordFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TextAnalyzerTest {

    private WordCounter createWordCounter() {
        return new WordCounter(List.of(
                new LengthWordFilter(3),
                new StopWordFilter()));
    }

    @Test
    public void testNormalizationAndCounting(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        String content = "Hello, world! This is a test. World is correct.";
        Files.writeString(file, content);

        TextReader reader = new TextReader();
        String normalized;
        try (Stream<String> lines = Files.lines(file)) {
            Stream<String> stream = reader.readAndNormalize(lines);
            normalized = stream.collect(java.util.stream.Collectors.joining(" "));
        }

        // "hello world this is a test world is correct" (spaces might vary but trimmed)
        // normalized should be lowercase alphanumeric
        assertFalse(normalized.contains(","));
        assertFalse(normalized.contains("!"));
        assertTrue(normalized.startsWith("hello"));

        WordCounter counter = createWordCounter();
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
        WordCounter counter = createWordCounter();
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
        WordCounter counter = createWordCounter();
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
    public void testReadAndNormalizeNullStream() {
        TextReader reader = new TextReader();
        Stream<String> result = reader.readAndNormalize(null);
        assertEquals(0, result.count(), "Should safely handle null stream by returning empty stream");
    }

    @Test
    public void testCountWordsNullStream() {
        WordCounter counter = createWordCounter();
        Map<String, Integer> result = counter.countWords((Stream<String>) null);
        assertTrue(result.isEmpty(), "Null stream should return empty map");
    }

    @Test
    public void testCountWordsEmptyText() {
        WordCounter counter = createWordCounter();
        Map<String, Integer> result = counter.countWords("");
        assertTrue(result.isEmpty(), "Empty string should return empty map");

        result = counter.countWords((String) null);
        assertTrue(result.isEmpty(), "Null string should return empty map");
    }

    @Test
    public void testTextWithOnlyStopAndShortWords() {
        WordCounter counter = createWordCounter();
        String text = "a the is at do be me hi ok";
        Map<String, Integer> result = counter.countWords(text);
        assertTrue(result.isEmpty(), "Text containing only stop and short words should yield empty map");
    }

    @Test
    public void testGetWordsStartingWithNullOrNonExistentPrefix() {
        WordCounter counter = createWordCounter();
        Map<String, Integer> counts = counter.countWords("apple banana cherry");

        List<String> nullPrefixResult = counter.getWordsStartingWith(counts, null);
        assertTrue(nullPrefixResult.isEmpty(), "Null prefix should return empty list");

        List<String> noMatchResult = counter.getWordsStartingWith(counts, "z");
        assertTrue(noMatchResult.isEmpty(), "Non-existent prefix should return empty list");
    }

    @Test
    public void testGetTopNWordsWhenNGreaterThanUniqueWords() {
        WordCounter counter = createWordCounter();
        Map<String, Integer> counts = counter.countWords("apple apple banana");

        // Only 2 unique words. Requesting top 10 should safely return 2 items.
        List<Map.Entry<String, Integer>> topWords = counter.getTopNWords(counts, 10);
        assertEquals(2, topWords.size(), "Should safely return all unique words if N > unique words");
        assertEquals("apple", topWords.get(0).getKey());
    }
}
