package com.example;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

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

    private static double stringSimilarity(String a, String b, LevenshteinDistance ld) {
        int maxLength = Math.max(a.length(), b.length());
        if (maxLength == 0) return 1.0;
        return 1.0 - (double) ld.apply(a, b) / maxLength;
    }

    private static String classifyPrecision(double score) {
        if (score >= 0.85) return MatchResult.PRECISION_HIGH;
        if (score >= 0.70) return MatchResult.PRECISION_MEDIUM;
        return MatchResult.PRECISION_LOW;
    }
}