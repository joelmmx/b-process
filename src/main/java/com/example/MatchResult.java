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
}