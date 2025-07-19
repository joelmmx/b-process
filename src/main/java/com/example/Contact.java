package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a contact entry parsed from the input Excel file.
 *
 * Each contact includes identifying information such as name, email, postal code, and address,
 * along with the original row index from the Excel sheet to preserve data traceability.
 *
 * This class is used in the duplicate detection logic to evaluate similarity between contact records.
 */
@AllArgsConstructor
@ToString
@Getter
public class Contact {
    private int contactID;
    private String name;
    private String name1;
    private String email;
    private String postalZip;
    private String address;
    private int rowIndex;
}