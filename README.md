# Duplicate Contact Detector

A Java-based utility that reads an Excel spreadsheet of contacts and intelligently detects potential duplicate entries using a weighted similarity algorithm with **Levenshtein Distance**.

---

## Purpose

This tool was designed to support contact data cleansing by detecting duplicate records based on a configurable similarity model. It's useful for CRMs, customer databases, mailing lists, and other systems that require contact deduplication.

---

## Features

- ✅ Reads `.xlsx` contact files
- ✅ Detects duplicates using weighted scoring:
  - **Email match (60%)**
  - **Name match (20%)**
  - **Postal ZIP match (10%)**
  - **Address match (10%)**
- ✅ Classifies results into:
  - `PRECISION_HIGH`
  - `PRECISION_MEDIUM`
  - `PRECISION_LOW`
- ✅ Outputs clear match results to the console
- ✅ Fully covered by **unit tests** using JUnit
- ✅ Logs output using Log4j

---

## ⚙How It Works

### Levenshtein Distance

The tool uses the [Levenshtein Distance](https://en.wikipedia.org/wiki/Levenshtein_distance) algorithm to measure similarity between names and addresses.

For example:
- `"Smith"` vs. `"Smyth"` → Distance: `1`
- `"123 Main St"` vs. `"123 Main Street"` → Similarity: `0.85`

A final weighted score is calculated, and the match is classified as HIGH / MEDIUM / LOW accordingly.

---

## Technologies Used

- **Java 17+**
- **Apache POI** for Excel parsing
- **Apache Commons Text** for Levenshtein similarity
- **JUnit 5** for testing
- **Log4j 2** for logging

---

## 📁 Project Structure

```
.
├── src
│   └── main/java/com/example/
│       ├── Main.java            # Core logic
│       ├── Contact.java         # Contact model
│       ├── ContactReader.java   # Excel reader
│       └── MatchResult.java     # Match DTO
│
├── test/java/com/example/
│   └── MainTest.java            # Full suite of 50+ test cases
│
├── pom.xml                      # Maven dependencies
└── Code Assessment - Find Duplicates Input.xlsx
```
---

## Getting Started

### Requirements
- Java 17+
- Maven 3+

### ▶ Run the project

```bash
mvn clean compile exec:java -Dexec.mainClass="com.example.Main"

mvn test

INFO  ContactID Origen | ContactID Coincidencia | Precisión
INFO  1                2                         HIGH
INFO  21               22                        MEDIUM
INFO  43               44                        LOW
