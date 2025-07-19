package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MatchResult {
    private int originID;
    private int matchedID;
    private String precision;

    public static final String PRECISION_HIGH = "Alta";
    public static final String PRECISION_MEDIUM = "Media";
    public static final String PRECISION_LOW = "Baja";
}