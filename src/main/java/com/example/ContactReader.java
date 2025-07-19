package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContactReader {

    private static final Logger logger = LogManager.getLogger(ContactReader.class);

    private static final int ID_COL = 0;
    private static final int NAME_COL = 1;
    private static final int NAME1_COL = 2;
    private static final int EMAIL_COL = 3;
    private static final int ZIP_COL = 4;
    private static final int ADDRESS_COL = 5;

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
        }

        return contacts;
    }

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