package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the result of a duplicate contact detection between two contact entries.
 *
 * Contains the IDs of the original and matched contacts along with a precision label
 * indicating how strong the match is based on similarity scoring.
 *
 * Precision levels:
 * - PRECISION_HIGH ("Alta"): Strong match across most fields
 * - PRECISION_MEDIUM ("Media"): Partial match with some differences
 * - PRECISION_LOW ("Baja"): Weak match, few similarities
 *
 * This class is used as a result structure for match evaluations in the system.
 */
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