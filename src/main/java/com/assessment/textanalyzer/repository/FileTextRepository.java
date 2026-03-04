package com.assessment.textanalyzer.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileTextRepository implements TextRepository {

    @Override
    public Stream<String> readLines(String filePath) throws IOException {
        return Files.lines(Path.of(filePath));
    }
}
