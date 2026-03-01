package com.assessment.textanalyzer;

import com.assessment.textanalyzer.model.StatisticsReport;
import com.assessment.textanalyzer.service.TextReader;
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

        TextReader reader = new TextReader();
        String normalized = reader.readAndNormalize(file.toString());
        
        // "hello  world  this is a test  world is correct" (spaces might vary but trimmed)
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
}
