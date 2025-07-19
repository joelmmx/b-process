package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test class for verifying contact similarity detection in the Main.findMatches method.
 *
 * This test suite includes various test cases to validate matching logic under different conditions:
 * - HIGH precision: contacts that are almost identical or differ slightly in format (e.g., "St" vs. "Street")
 * - MEDIUM precision: contacts with partial similarity such as name variation or slight address mismatch
 * - LOW precision or no match: contacts that differ significantly across most fields
 *
 * These tests ensure that the matching algorithm behaves correctly for a range of real-world contact data scenarios.
 */
public class MainTest {

    /**
     * All fields match exactly or are very similar (street abbreviation), expect HIGH precision
     */
    @Test
    public void testHighMatch1() {
        Contact c1 = new Contact(1, "John", "Smith", "john.smith@email.com", "12345", "123 Apple St", 1);
        Contact c2 = new Contact(2, "John", "Smith", "john.smith@email.com", "12345", "123 Apple Street", 2);
        List<MatchResult> results = Main.findMatches(Arrays.asList(c1, c2));
        Assertions.assertEquals(MatchResult.PRECISION_HIGH, results.get(0).getPrecision());
    }

    /**
     * Very similar full name, identical email, and minor address variation – expect HIGH precision
     */
    @Test
    public void testHighMatch2() {
        Contact c1 = new Contact(3, "Maria", "Lopez", "maria.lopez@mail.com", "67890", "45 Lakeview Rd", 1);
        Contact c2 = new Contact(4, "Maria", "Lopez", "maria.lopez@mail.com", "67890", "45 Lake View Road", 2);
        List<MatchResult> results = Main.findMatches(Arrays.asList(c1, c2));
        Assertions.assertEquals(MatchResult.PRECISION_HIGH, results.get(0).getPrecision());
    }

    /**
     * First name variation (Alicia vs Alice), zip and address also differ slightly – expect MEDIUM precision
     */
    @Test
    public void testMediumMatch1() {
        Contact c1 = new Contact(21, "Alice", "Walker", "alice.w@email.com", "11111", "5 Elm St", 1);
        Contact c2 = new Contact(22, "Alicia", "Walker", "alice.w@email.com", "11112", "5 Elm Street", 2);
        List<MatchResult> results = Main.findMatches(Arrays.asList(c1, c2));
        Assertions.assertEquals(MatchResult.PRECISION_MEDIUM, results.get(0).getPrecision());
    }

    /**
     * Minor variation in first name (Brian vs Bryan), full match on email, zip, and address format – expect HIGH precision
     */
    @Test
    public void testHighMatch3() {
        Contact c1 = new Contact(23, "Brian", "Choi", "bchoi@gmail.com", "44556", "789 River Blvd", 1);
        Contact c2 = new Contact(24, "Bryan", "Choi", "bchoi@gmail.com", "44556", "789 River Boulevard", 2);
        List<MatchResult> results = Main.findMatches(Arrays.asList(c1, c2));
        Assertions.assertEquals(MatchResult.PRECISION_HIGH, results.get(0).getPrecision());
    }

    /**
     * First and last names differ slightly, emails match, but zip and address differ – expect MEDIUM precision
     */
    @Test
    public void testLowMatch1() {
        Contact c1 = new Contact(41, "Carla", "Ramos", "carla@mail.com", "99887", "99 Sunset Rd", 1);
        Contact c2 = new Contact(42, "Carlos", "Ramon", "carla@mail.com", "99888", "98 Sunrise Ave", 2);
        List<MatchResult> results = Main.findMatches(Arrays.asList(c1, c2));
        Assertions.assertEquals(MatchResult.PRECISION_MEDIUM, results.get(0).getPrecision());
    }

    /**
     * First name, email, zip, and address all differ – no match expected
     */
    @Test
    public void testLowMatch2() {
        Contact c1 = new Contact(43, "David", "Nguyen", "dnguyen@aol.com", "23456", "123 Pine Ln", 1);
        Contact c2 = new Contact(44, "Davin", "Nguyen", "dnguyen2@aol.com", "23457", "127 Pine Lane", 2);
        List<MatchResult> results = Main.findMatches(Arrays.asList(c1, c2));
        Assertions.assertTrue(results.isEmpty()); // No match expected
    }
}