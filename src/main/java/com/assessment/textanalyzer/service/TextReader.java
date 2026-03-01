package com.assessment.textanalyzer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextReader {

    public String readAndNormalize(String filePath) throws IOException {
        String content = Files.readString(Path.of(filePath));
        
        
        return content.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ") 
                .trim();
    }
}
