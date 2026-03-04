package com.assessment.textanalyzer.repository;

import java.io.IOException;
import java.util.stream.Stream;

public interface DocumentRepository {
    Stream<String> readLines(String documentIdentifier) throws IOException;
}
