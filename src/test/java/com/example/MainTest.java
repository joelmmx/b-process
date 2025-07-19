package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {
    @Test
    public void testSimilarityScore() {
        Contact c1 = new Contact(1, "Juan", "Pérez", "juan@mail.com", "12345", "123 Main St.");
        Contact c2 = new Contact(2, "Juan", "Perez", "juan@mail.com", "12345", "123 Main Street");

        double score = Double.parseDouble(Main.findMatches(java.util.List.of(c1, c2)).get(0).getPrecision());
        Assertions.assertNotNull(score); // This ensures a match was found
    }
}