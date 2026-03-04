package com.assessment.textanalyzer.repository;

import java.io.IOException;
import java.util.stream.Stream;

public interface TextRepository {
    Stream<String> readLines(String source) throws IOException;
}
