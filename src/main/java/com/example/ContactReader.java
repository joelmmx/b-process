package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for reading contact information from an Excel (.xlsx) file.
 *
 * This class uses Apache POI to parse the first sheet of the Excel file and extract
 * contact data row-by-row, converting each into a {@link Contact} object.
 *
 * Expected column order in the Excel sheet:
 * 0 - Contact ID (numeric)
 * 1 - First Name
 * 2 - Last Name
 * 3 - Email
 * 4 - Postal Zip
 * 5 - Address
 *
 * Rows with missing or invalid primary information (ID, Name, Email) are skipped.
 *
 * The parsed contact list is returned for further processing by the matching logic.
 */
public class ContactReader {

    private static final Logger logger = LogManager.getLogger(ContactReader.class);

    private static final int ID_COL = 0;
    private static final int NAME_COL = 1;
    private static final int NAME1_COL = 2;
    private static final int EMAIL_COL = 3;
    private static final int ZIP_COL = 4;
    private static final int ADDRESS_COL = 5;

    /**
     * Reads contact data from the specified Excel file (.xlsx format).
     *
     * This method opens the file at the given path, reads the first sheet, and
     * iterates through each row (starting after the header) to extract relevant fields.
     * It constructs a {@link Contact} object from each valid row and adds it to the returned list.
     *
     * Rows missing key fields (ID, Name, Email) are skipped and logged.
     *
     * @param filePath the path to the Excel file to read
     * @return a list of valid {@link Contact} instances extracted from the file
     */
    public static List<Contact> readContactsFromExcel(String filePath) {
        List<Contact> contacts = new ArrayList<>();

        try (InputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rows; i++) { // skip header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String rawId = getCellValue(row.getCell(ID_COL));
                int contactID = rawId.matches("\\d+") ? Integer.parseInt(rawId) : 0;

                String name = getCellValue(row.getCell(NAME_COL));
                String name1 = getCellValue(row.getCell(NAME1_COL));
                String email = getCellValue(row.getCell(EMAIL_COL));
                String postalZip = getCellValue(row.getCell(ZIP_COL));
                String address = getCellValue(row.getCell(ADDRESS_COL));

                if (contactID == 0 && name.isBlank() && name1.isBlank() && email.isBlank()) {
                    logger.warn("Skipping blank or incomplete row at index {}", i);
                    continue;
                }

                final String contactSummary = String.format(
                        "[ID=%s, Name=%s %s, Email=%s, Zip=%s, Addr=%s]",
                        rawId, name, name1, email, postalZip, address
                );

                logger.debug("Parsed contact: {}", contactSummary);

                contacts.add(new Contact(contactID, name, name1, email, postalZip, address, i + 1));
            }

        } catch (Exception e) {
            logger.error("Error reading Excel file: {}", filePath, e);
            return null;
        }

        return contacts;
    }

    /**
     * Extracts a string value from a given Excel cell.
     *
     * Supports multiple cell types including STRING, NUMERIC, BOOLEAN, and FORMULA.
     * Falls back to empty string on error or if the cell is null.
     *
     * @param cell the Excel cell to extract the value from
     * @return the string representation of the cell value, or empty string if not readable
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    try {
                        yield cell.getStringCellValue().trim();
                    } catch (IllegalStateException e) {
                        yield String.valueOf(cell.getNumericCellValue());
                    }
                }
                default -> "";
            };
        } catch (Exception e) {
            logger.warn("Failed to parse cell: {}", cell, e);
            return "";
        }
    }
}