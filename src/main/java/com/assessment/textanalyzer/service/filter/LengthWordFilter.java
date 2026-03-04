package com.assessment.textanalyzer.service.filter;

public class LengthWordFilter implements WordFilter {
    private final int minLength;

    public LengthWordFilter(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean isValid(String word) {
        return word != null && word.length() >= minLength;
    }
}
