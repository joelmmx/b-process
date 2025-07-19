/**
 * Main class for detecting duplicate or similar contacts from an Excel input file.
 *
 * The application reads contact data from an Excel file and compares all unique pairs
 * using a weighted similarity scoring system based on:
 * - Email (60% weight, exact match)
 * - Full name (20% weight, Levenshtein similarity)
 * - Zip code (10% weight, exact match)
 * - Address (10% weight, Levenshtein similarity)
 *
 * Based on the total similarity score, matches are classified into HIGH, MEDIUM, or LOW precision,
 * or excluded if below the threshold.
 *
 * Matched results are logged to the console with their corresponding contact IDs and precision level.
 */
package com.example;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Main execution method for the contact matching application.
     *
     * Reads an Excel file containing contact data, processes all unique pairs
     * using weighted similarity scoring, and logs matched results to the console.
     * Only pairs with a similarity score above 0.5 are considered matches.
     */
    public static void main(String[] args) {
        String filePath = "Code Assessment - Find Duplicates Input.xlsx";
        List<Contact> contacts = ContactReader.readContactsFromExcel(filePath);
        List<MatchResult> results = findMatches(contacts);

        logger.info("ContactID Origen | ContactID Coincidencia | Precisión");
        for (MatchResult result : results) {
            String origin = String.format("%-17d", result.getOriginID());
            String match = String.format("%-25d", result.getMatchedID());
            logger.info("{}{}{}", origin, match, result.getPrecision());
        }
    }

    /**
     * Finds and returns a list of matching contact pairs based on a weighted similarity score.
     *
     * The score is calculated using:
     * - Email match (exact): 60%
     * - Full name similarity (Levenshtein): 20%
     * - Zip code match (exact): 10%
     * - Address similarity (Levenshtein): 10%
     *
     * Matches are categorized into HIGH, MEDIUM, or LOW precision based on thresholds.
     *
     * @param contacts the list of contacts to evaluate
     * @return a list of match results with precision classifications
     */
    public static List<MatchResult> findMatches(List<Contact> contacts) {
        List<MatchResult> results = new ArrayList<>();
        LevenshteinDistance ld = new LevenshteinDistance();

        for (int i = 0; i < contacts.size(); i++) {
            Contact c1 = contacts.get(i);
            for (int j = i + 1; j < contacts.size(); j++) {
                Contact c2 = contacts.get(j);

                double nameScore = stringSimilarity(
                        (c1.getName() + " " + c1.getName1()).toLowerCase(),
                        (c2.getName() + " " + c2.getName1()).toLowerCase(),
                        ld
                );

                double emailScore = (!c1.getEmail().isBlank() && c1.getEmail().equalsIgnoreCase(c2.getEmail())) ? 1.0 : 0.0;
                double zipScore = (!c1.getPostalZip().isBlank() && c1.getPostalZip().equals(c2.getPostalZip())) ? 1.0 : 0.0;

                double addressScore = 0.0;
                if (!c1.getAddress().isBlank() && !c2.getAddress().isBlank()) {
                    addressScore = stringSimilarity(c1.getAddress().toLowerCase(), c2.getAddress().toLowerCase(), ld);
                }

                double totalScore = (emailScore * 0.6) + (nameScore * 0.2) + (zipScore * 0.1) + (addressScore * 0.1);

                if (totalScore >= 0.5) {
                    String precision = classifyPrecision(totalScore);
                    results.add(new MatchResult(c1.getContactID(), c2.getContactID(), precision));
                }
            }
        }

        return results;
    }

    /**
     * Calculates the normalized Levenshtein similarity score between two strings.
     *
     * Levenshtein Distance measures the minimum number of single-character edits
     * (insertions, deletions, or substitutions) required to change one string into the other.
     *
     * This method converts that distance into a similarity score between 0.0 and 1.0 by
     * normalizing it over the maximum length of the two strings:
     *
     *     similarity = 1 - (levenshtein_distance / max_length)
     *
     * A score of 1.0 means the strings are identical, while 0.0 indicates no similarity.
     *
     * Example:
     *   Comparing "Main Street" vs. "Main St":
     *   - Levenshtein distance = 6 (needs 6 edits to make the strings equal)
     *   - max_length = 11 ("Main Street")
     *   - similarity = 1 - (6 / 11) ≈ 0.4545
     *
     *   Comparing "John Smith" vs. "Jon Smith":
     *   - Levenshtein distance = 1 (change 'h' to nothing)
     *   - max_length = 10
     *   - similarity = 1 - (1 / 10) = 0.9
     *
     * @param a the first string to compare
     * @param b the second string to compare
     * @param ld LevenshteinDistance instance used for edit distance calculation
     * @return a normalized similarity score between 0.0 (completely different) and 1.0 (identical)
     */
    private static double stringSimilarity(String a, String b, LevenshteinDistance ld) {
        int maxLength = Math.max(a.length(), b.length());
        if (maxLength == 0) return 1.0;
        return 1.0 - (double) ld.apply(a, b) / maxLength;
    }

    /**
     * Classifies the similarity score into a precision category.
     *
     * @param score the total similarity score
     * @return one of "Alta", "Media", or "Baja" based on predefined thresholds
     */
    private static String classifyPrecision(double score) {
        if (score >= 0.85) return MatchResult.PRECISION_HIGH;
        if (score >= 0.70) return MatchResult.PRECISION_MEDIUM;
        return MatchResult.PRECISION_LOW;
    }
}